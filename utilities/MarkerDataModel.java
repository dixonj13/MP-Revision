/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author John
 */
public class MarkerDataModel {

    public static final int VAMP_UNDEFINED = -2;
    public static final int VAMP_INFINITE = -1;

    private MediaPlayer mp;
    private NavigableMap<Duration, MarkerBean> navMap;
    private ObservableList<MarkerBean> obs;
    private SortedList<MarkerBean> items;
    private ObservableList<Vamp> vamps;

    private int prevMarker;
    private int currMarker;
    private int nextMarker;

    private Boolean inVampZone;
    private Vamp currVamp;

    public MarkerDataModel(MediaPlayer mpRef, File source) throws IOException {
        mp = mpRef;
        inVampZone = false;
        currVamp = null;
        vamps = FXCollections.observableArrayList();
        navMap = new TreeMap<>((Duration o1, Duration o2) -> {
            return (int) ((o1.toMillis() * 100) - (o2.toMillis() * 100));
        });

        obs = FXCollections.observableArrayList(MarkerBean.extractor());
        items = new SortedList<>(obs, (MarkerBean o1, MarkerBean o2) -> {
            return (int) (o1.getTime().toMillis() - o2.getTime().toMillis());
        });

        items.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                while (c.next()) {
                    if (c.wasUpdated()) {
                        // check this
                    } else {
                        for (Object rem : c.getRemoved()) {
                            navMap.remove(((MarkerBean) rem).getTime());
                            int remSize = c.getRemovedSize();
                            int listSize = items.size();
                            for (int i = c.getTo(); i < listSize; i++) {
                                navMap.get(items.get(i).getTime()).decreaseIndexBy(remSize);
                            }
                        }
                        for (Object add : c.getAddedSubList()) {
                            navMap.put(((MarkerBean) add).getTime(), (MarkerBean) add);
                            int addSize = c.getAddedSize();
                            int listSize = items.size();
                            int to = c.getTo();
                            for (int i = c.getFrom(); i < to; i++) {
                                navMap.get(items.get(i).getTime()).setIndex(i);
                            }
                            for (int i = to; i < listSize; i++) {
                                navMap.get(items.get(i).getTime()).increaseIndexBy(addSize);
                            }
                        }
                    }
                }
            }
        });

        mp.currentTimeProperty().addListener((o) -> {
            int index = navMap.floorEntry(mp.getCurrentTime()).getValue().getIndex();

            if (!inVampZone) {
                if (checkVampZone()) {
                    mp.setStartTime(currVamp.getStartTime());
                    mp.setStopTime(currVamp.getStopTime());
                }
            }

            if (currMarker != index) {
                items.get(currMarker).setPlaying(false);
                items.get(index).setPlaying(true);
                prevMarker = (index > 0) ? (index - 1) : 0;
                currMarker = index;
                nextMarker = (index + 1) % (items.size());
            }
        });

        importMarkers(source);
        prevMarker = 0;
        currMarker = 0;
        nextMarker = (items.size() > 1) ? 1 : 0;

        if (!navMap.containsKey(new Duration(0))) {
            MarkerBean start = new MarkerBean("Start", new Duration(0));
            start.setPlaying(true);
            addMarker(start);
        }

        safeAddVamp(2, 3, 2);
    }

    private void importMarkers(File source) throws IOException {
        Map<String, Duration> map = ChapterParser.parse(source);

        for (Map.Entry<String, Duration> e : map.entrySet()) {
            obs.add(new MarkerBean(e.getKey(), e.getValue()));
        }
    }

    public final void addMarker(MarkerBean m) {
        obs.add(m);
    }

    public final void addMarker(String title, Duration time) {
        obs.add(new MarkerBean(title, time));
    }

    public final void removeMarker(int index) {
        obs.remove(index);
    }

    public SortedList<MarkerBean> getItems() {
        return items;
    }

    public void setItems(SortedList<MarkerBean> items) {
        this.items = items;
    }

    public MarkerBean getPrevMarker() {
        return items.get(prevMarker);
    }

    public MarkerBean getCurrMarker() {
        return items.get(currMarker);
    }

    public MarkerBean getNextMarker() {
        return items.get(nextMarker);
    }

    public final Boolean inVampZone() {
        return inVampZone;
    }

    public final Boolean checkVampZone() {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (inRange(currMarker, curr.getStartIndex(),
                    curr.getStopIndex())) {
                currVamp = curr;
                return inVampZone = true;
            }
        }
        return inVampZone = false;
    }

    public void updateVampCycle() {
        int repeats = currVamp.getRepeatsRemaining();
        System.out.println("Before: " + repeats);
        if (repeats > 0) {
            currVamp.decrementRepeatsRemaining();
            System.out.println("After: " + currVamp.getRepeatsRemaining());
        } else if (repeats == 0) {
            safeRemoveVamp(currVamp.getStartIndex(), currVamp.getStopIndex());
            inVampZone = false;
            currVamp = null;
            mp.setStartTime(new Duration(0));
            mp.setStopTime(mp.getMedia().getDuration());
        }
    }

    public void safeAddVamp(int start, int end, int repeats) {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (doRangesOverlap(items.get(start).getIndex(),
                    items.get(end).getIndex(), curr.getStartIndex(),
                    curr.getStopIndex())) {
                removeVamp(i);
            }
        }
        vamps.add(new Vamp(start, end, repeats));
        for (int i = start; i <= end; i++) {
            items.get(i).setVamped(true);
            System.out.printf("Index %d: Vamped = %b\n", i, items.get(i).isVamped());
        }
    }

    public void safeRemoveVamp(int start, int end) {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (doRangesOverlap(items.get(start).getIndex(),
                    items.get(end).getIndex(), curr.getStartIndex(),
                    curr.getStopIndex())) {
                removeVamp(i);
                break;
            }
        }
    }

    private void removeVamp(int index) {
        Vamp temp = vamps.get(index);
        vamps.remove(index);
        for (int i = temp.getStartIndex(); i <= temp.getStopIndex(); i++) {
            items.get(i).setVamped(false);
            System.out.printf("Index %d: Vamped = %b\n", i, items.get(i).isVamped());
        }
    }

    private Boolean doRangesOverlap(int x1, int x2, int y1, int y2) {
        return (x1 <= y2) && (y1 <= x2);
    }

    private Boolean inRange(int index, int x1, int y1) {
        return (x1 <= index) && (index <= y1);
    }

    public class Vamp {

        private final Duration start;
        private final Duration stop;
        private final int startIndex;
        private final int stopIndex;
        private int repeatsRemaining;

        public Vamp(int start, int stop, int repeats) {
            startIndex = start;
            stopIndex = stop;
            this.start = items.get(start).getTime();
            this.stop = (stop < items.size() - 1)
                    ? items.get(stop + 1).getTime().subtract(new Duration(10))
                    : mp.getMedia().getDuration();
            repeatsRemaining = repeats;
        }

        public Boolean isVampInfinite() {
            return repeatsRemaining == -1;
        }

        public int getRepeatsRemaining() {
            return repeatsRemaining;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getStopIndex() {
            return stopIndex;
        }

        public Duration getStartTime() {
            return start;
        }

        public Duration getStopTime() {
            return stop;
        }

        public void decrementRepeatsRemaining() {
            repeatsRemaining--;
        }
    }
}
