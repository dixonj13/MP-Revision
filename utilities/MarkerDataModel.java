/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
    private Map<String, Duration> map;
    private ObservableMap<String, Duration> markerMap;
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
        vamps = FXCollections.observableArrayList();

        obs = FXCollections.observableArrayList(MarkerBean.extractor());
        items = new SortedList<>(obs, (MarkerBean o1, MarkerBean o2) -> {
            return (int) (o1.getTime().toMillis() - o2.getTime().toMillis());
        });

        importMarkers(source);
        prevMarker = 0;
        currMarker = 0;
        nextMarker = (items.size() > 1) ? 1 : 0;

        if (!map.containsValue(new Duration(0))) {
            MarkerBean start = new MarkerBean("Start", new Duration(0));
            start.setPlaying(true);
            addMarker(start);
        }
    }

    private void importMarkers(File source) throws IOException {
        markerMap = mp.getMedia().getMarkers();
        map = ChapterParser.parse(source);
        markerMap = FXCollections.observableMap(map);

        for (Map.Entry<String, Duration> e : map.entrySet()) {
            obs.add(new MarkerBean(e.getKey(), e.getValue()));
        }
    }

    public void updateMarkerPlaying(int index) {
        items.get(currMarker).setPlaying(false);
        items.get(index).setPlaying(true);
        prevMarker = (index > 0) ? (index - 1) : 0;
        currMarker = index;
        nextMarker = (index + 1) % (items.size());
    }

    public final void addMarker(MarkerBean m) {
        map.put(m.getTitle(), m.getTime());
        obs.add(m);
    }

    public final void addMarker(String title, Duration time) {
        map.put(title, time);
        obs.add(new MarkerBean(title, time));
    }

    public final void removeMarker(MarkerBean m) {
        String key = m.getTitle();
        if (map.containsKey(key)) {
            map.remove(key);
            obs.remove(m);
        }
    }

    public ObservableMap<String, Duration> getMarkers() {
        return markerMap;
    }

    public void setMarkers(ObservableMap<String, Duration> markerMap) {
        this.markerMap = markerMap;
    }

    public SortedList<MarkerBean> getItems() {
        return items;
    }

    public void setItems(SortedList<MarkerBean> items) {
        this.items = items;
    }

    public int getPrevMarkerIndex() {
        return prevMarker;
    }

    public int getCurrMarkerIndex() {
        return currMarker;
    }

    public int getNextMarkerIndex() {
        return nextMarker;
    }

    public Duration getPrevMarker() {
        return items.get(prevMarker).getTime();
    }

    public Duration getCurrMarker() {
        return items.get(currMarker).getTime();
    }

    public Duration getNextMarker() {
        return items.get(nextMarker).getTime();
    }

    public Vamp getVampZone(int index) {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (inRange(items.get(index).getTime(), curr.getStartTime(),
                    curr.getStopTime())) {
                return curr;
            }
        }
        return null;
    }

    public Boolean inVamp() {
        return inVampZone;
    }

    public int vampsLeft() {
        if (currVamp == null) {
            return -2;
        }
        return currVamp.getRepeatsRemaining();
    }

    public int getVampStart() {
        if (currVamp == null) {
            return -2;
        }
        return currVamp.getStart();
    }

    public int getVampEnd() {
        if (currVamp == null) {
            return -2;
        }
        return currVamp.getEnd();
    }

    public void updateVampCycle() {
        int repeats = currVamp.getRepeatsRemaining();
        if (repeats > 0) {
            currVamp.decrementRepeatsRemaining();
        } else if (repeats == 0) {
            vamps.remove(currVamp);
            inVampZone = false;
            currVamp = null;
        }
    }

    private void safeAddVamp(int start, int end, int repeats) {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (doRangesOverlap(items.get(start).getTime(),
                    items.get(end).getTime(), curr.getStartTime(),
                    curr.getStopTime())) {
                vamps.remove(i);
            }
        }
        vamps.add(new Vamp(start, end, repeats));
    }

    private void safeRemoveVamp(int start, int end) {
        int size = vamps.size();
        for (int i = 0; i < size; i++) {
            Vamp curr = vamps.get(i);
            if (doRangesOverlap(items.get(start).getTime(),
                    items.get(end).getTime(), curr.getStartTime(),
                    curr.getStopTime())) {
                vamps.remove(i);
                break;
            }
        }
    }

    private Boolean doRangesOverlap(Duration x1, Duration x2, Duration y1,
            Duration y2) {
        return (x1.lessThanOrEqualTo(y2)) && (y1.lessThanOrEqualTo(x2));
    }

    private Boolean inRange(Duration index, Duration x1, Duration y1) {
        return (x1.lessThanOrEqualTo(index)) && (index.lessThanOrEqualTo(y1));
    }

    public class Vamp {

        private final Duration start;
        private final Duration stop;
        private int repeatsRemaining;

        public Vamp(int start, int end, int repeats) {
            this.start = items.get(start).getTime();
            this.stop = (end < items.size() - 1) ? items.get(end + 1).getTime()
                    : mp.getMedia().getDuration();
            repeatsRemaining = repeats;
        }

        public Boolean isVampInfinite() {
            return repeatsRemaining == -1;
        }

        public int getRepeatsRemaining() {
            return repeatsRemaining;
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
