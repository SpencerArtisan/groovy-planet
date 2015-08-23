package com.bigcustard.util;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class WatchableListTest {
    private WatchableList<String> subject;

    @Before
    public void before() {
        subject = new WatchableList<>(ImmutableList.of("one", "two"));
    }

    @Test
    public void itShould_NotifyNewListenersImmediately() {
        List<String> added = new ArrayList<>();
        subject.watchAdd(added::add);
        assertThat(added).containsExactly("one", "two");
    }

    @Test
    public void itShould_NotifyListenersWhenValueAdded() {
        List<String> added = new ArrayList<>();
        subject.watchAdd(added::add);
        subject.add("three");
        assertThat(added).containsExactly("one", "two", "three");
    }

    @Test
    public void itShould_NotifyListenersWhenValueRemoved() {
        List<String> removed = new ArrayList<>();
        subject.watchRemove(removed::add);
        subject.remove("one");
        assertThat(removed).containsExactly("one");
    }


}
