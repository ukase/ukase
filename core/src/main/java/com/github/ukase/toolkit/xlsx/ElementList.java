/*
 * Copyright (c) 2018 Pavel Uvarov <pauknone@yahoo.com>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.toolkit.xlsx;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Utility Class-wrapper that enables modern Collections Framework code writing capabilities
 * (including: for-each statements, streams)
 */
public class ElementList implements List<Element> {
    private final NodeList list;

    public ElementList(NodeList list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.getLength();
    }

    @Override
    public boolean isEmpty() {
        return size() < 1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<Element> iterator() {
        return new ElementListIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size()];
        for (int i = 0 ; i < size() ; i++) {
            arr[i] = get(i);
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean add(Element element) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o: c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Element> c) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Element> c) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void clear() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Element get(int index) {
        return (Element) list.item(index);
    }

    @Override
    public Element set(int index, Element element) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void add(int index, Element element) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public Element remove(int index) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public int indexOf(Object o) {
        if (o == null || !(o instanceof Element)) {
            return -1;
        }
        for (int i = 0 ; i < size() ; i++) {
            if (o.equals(get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null || !(o instanceof Element)) {
            return -1;
        }
        for (int i = size() - 1 ; i >= 0 ; i--) {
            if (o.equals(get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Element> listIterator() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public ListIterator<Element> listIterator(int index) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public List<Element> subList(int fromIndex, int toIndex) {
        throw new RuntimeException("not implemented yet");
    }

    private class ElementListIterator implements Iterator<Element> {
        private int current = 0;

        @Override
        public void remove() {
            throw new RuntimeException("not implemented yet");
        }

        @Override
        public boolean hasNext() {
            return current < size();
        }

        @Override
        public Element next() {
            return get(current++);
        }

        @Override
        public void forEachRemaining(Consumer<? super Element> action) {
            while(hasNext()) {
                action.accept(next());
            }
        }
    }
}
