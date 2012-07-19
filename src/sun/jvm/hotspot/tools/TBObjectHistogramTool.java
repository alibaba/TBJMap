package sun.jvm.hotspot.tools;

import java.io.PrintStream;

import sun.jvm.hotspot.oops.TBObjectHistogram;
import sun.jvm.hotspot.oops.ObjectHeap;
import sun.jvm.hotspot.runtime.VM;

/**
 * A sample tool which uses the Serviceability Agent's APIs to obtain an object
 * histogram from a remote or crashed VM.
 */
public class TBObjectHistogramTool extends Tool {

	public void run() {
		run(System.out, System.err);
	}

	public void run(PrintStream out, PrintStream err) {
		// Ready to go with the database...
		ObjectHeap heap = VM.getVM().getObjectHeap();
		TBObjectHistogram histogram = new TBObjectHistogram();
		err.println("Iterating over heap. This may take a while...");
		long startTime = System.currentTimeMillis();
		heap.iterate(histogram);
		long endTime = System.currentTimeMillis();
		histogram.printOn(out);
		float secs = (float) (endTime - startTime) / 1000.0f;
		err.println("Heap traversal took " + secs + " seconds.");
	}

	public void printOld() {
		PrintStream out = System.out;
		PrintStream err = System.err;
		// Ready to go with the database...
		ObjectHeap heap = VM.getVM().getObjectHeap();
		TBObjectHistogram histogram = new TBObjectHistogram();
		err.println("Iterating over heap. This may take a while...");
		long startTime = System.currentTimeMillis();
		heap.iterate(histogram);
		long endTime = System.currentTimeMillis();
		histogram.printOnOld(out);
		float secs = (float) (endTime - startTime) / 1000.0f;
		err.println("Heap traversal took " + secs + " seconds.");
	}

	public static void main(String[] args) {
		TBObjectHistogramTool oh = new TBObjectHistogramTool();
		oh.start(args);
		oh.stop();
	}
}
