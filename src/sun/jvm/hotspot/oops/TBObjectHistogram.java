package sun.jvm.hotspot.oops;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TBObjectHistogram implements HeapVisitor {

    private HashMap<Klass, TBObjectHistogramElement> map;

    public TBObjectHistogram() {
        map = new HashMap<Klass, TBObjectHistogramElement>();
    }

    public void prologue(long size) {
    }

    public boolean doObj(Oop obj) {
        Klass klass = obj.getKlass();
        if (!map.containsKey(klass))
            map.put(klass, new TBObjectHistogramElement(klass));
        ((TBObjectHistogramElement) map.get(klass)).updateWith(obj);
        return false;
    }

    public void epilogue() {
    }

    /**
     * Call this after the iteration is complete to obtain the
     * ObjectHistogramElements in descending order of total heap size consumed
     * in the form of a List<ObjectHistogramElement>.
     */
    public List<TBObjectHistogramElement> getElements() {
        List<TBObjectHistogramElement> list = new ArrayList<TBObjectHistogramElement>();
        list.addAll(map.values());
        Collections.sort(list, new Comparator<TBObjectHistogramElement>() {
            public int compare(TBObjectHistogramElement o1, TBObjectHistogramElement o2) {
                return o1.compare(o2);
            }
        });
        return list;
    }

    public void print() {
        printOn(System.out);
    }

    public void printOn(PrintStream tty) {
        List<TBObjectHistogramElement> list = getElements();
        TBObjectHistogramElement.titleOn(tty);
        Iterator<TBObjectHistogramElement> iterator = list.listIterator();
        int num = 0;
        int totalCount = 0;
        int totalSize = 0;
        while (iterator.hasNext()) {
            TBObjectHistogramElement el = iterator.next();
            num++;
            totalCount += el.getCount();
            totalSize += el.getSize();
            tty.print(num + ":" + "\t");
            el.printOn(tty);
        }
        tty.println("Total : " + "\t" + totalCount + "\t" + totalSize);
    }

    public void printOnOld(PrintStream tty) {
        List<TBObjectHistogramElement> list = getElements();
        TBObjectHistogramElement.titleOnOld(tty);
        Iterator<TBObjectHistogramElement> iterator = list.listIterator();
        int num = 0;
        int totalOldCount = 0;
        int totalOldSize = 0;
        while (iterator.hasNext()) {
            TBObjectHistogramElement el = iterator.next();
            if (el.getOldCount() > 0) {
                totalOldCount += el.getOldCount();
                totalOldSize += el.getOldSize();
                num++;
                tty.print(num + ":" + "\t");
                el.printOnOld(tty);
            }
        }
        tty.println("Total : " + "\t" + totalOldCount + "\t" + totalOldSize);
    }

    public void printOnPerm(PrintStream tty) {
        List<TBObjectHistogramElement> list = getElements();
        TBObjectHistogramElement.titleOnPerm(tty);
        Iterator<TBObjectHistogramElement> iterator = list.listIterator();
        int num = 0;
        int totalPermCount = 0;
        int totalPermSize = 0;
        while (iterator.hasNext()) {
            TBObjectHistogramElement el = iterator.next();
            if (el.getPermCount() > 0) {
                totalPermCount += el.getPermCount();
                totalPermSize += el.getPermSize();
                num++;
                tty.print(num + ":" + "\t");
                el.printOnPerm(tty);
            }
        }
        tty.println("Total : " + "\t" + totalPermCount + "\t" + totalPermSize);
    }

}
