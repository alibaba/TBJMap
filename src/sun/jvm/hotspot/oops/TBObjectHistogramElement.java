package sun.jvm.hotspot.oops;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSOldGen;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSPermGen;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSYoungGen;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.ParallelScavengeHeap;
import sun.jvm.hotspot.gc_interface.CollectedHeap;
import sun.jvm.hotspot.memory.DefNewGeneration;
import sun.jvm.hotspot.memory.GenCollectedHeap;
import sun.jvm.hotspot.memory.Generation;
import sun.jvm.hotspot.runtime.VM;

public class TBObjectHistogramElement {
	private Klass klass;
	private long count; // Number of instances of klass
	private long size; // Total size of all these instances
	private long edenCount; // Number of instances of klass in eden
	private long edenSize; // Total size of all instances in eden
	private long survivorCount; // ditto in survivor
	private long survivorSize; // ditto in survivor
	private long oldCount; // ditto in old generation
	private long oldSize; // ditto in old generation
	private long permCount; // ditto in permanent generation
	private long permSize; // ditto in permanent generation
	private final static int Unknown = 0;
	private final static int InEden = 1;
	private final static int InSurvivor = 2;
	private final static int InOld = 3;
	private final static int InPerm = 4;

	public TBObjectHistogramElement(Klass k) {
		klass = k;
	}

	public void updateWith(Oop obj) {
		long objSize = obj.getObjectSize();
		count += 1;
		size += objSize;
		int result = getLocation(obj);
		switch (result) {
		case Unknown:
			break;
		case InEden:
			edenCount += 1;
			edenSize += objSize;
			break;
		case InSurvivor:
			survivorCount += 1;
			survivorSize += objSize;
			break;
		case InOld:
			oldCount += 1;
			oldSize += objSize;
			break;
		case InPerm:
			permCount += 1;
			permSize += objSize;
			break;
		default:
			break;
		}
	}

	public int compare(TBObjectHistogramElement other) {
		return (int) (other.size - size);
	}

	/** Klass for this ObjectHistogramElement */
	public Klass getKlass() {
		return klass;
	}

	/** Number of instances of klass */
	public long getCount() {
		return count;
	}

	/** Total size of all these instances */
	public long getSize() {
		return size;
	}

	/** Number of old instances of klass */
	public long getOldCount() {
		return oldCount;
	}
	/** Total size of all old instances*/
	public long getOldSize() {
		return oldSize;
	}

	private String getInternalName(Klass k) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		getKlass().printValueOn(new PrintStream(bos));
		// '*' is used to denote VM internal klasses.
		return "* " + bos.toString();
	}

	/** Human readable description **/
	public String getDescription() {
		Klass k = getKlass();
		if (k instanceof InstanceKlass) {
			return k.getName().asString().replace('/', '.');
		} else if (k instanceof ArrayKlass) {
			ArrayKlass ak = (ArrayKlass) k;
			if (k instanceof TypeArrayKlass) {
				TypeArrayKlass tak = (TypeArrayKlass) ak;
				return tak.getElementTypeName() + "[]";
			} else if (k instanceof ObjArrayKlass) {
				ObjArrayKlass oak = (ObjArrayKlass) ak;
				// See whether it's a "system objArray"
				if (oak.equals(VM.getVM().getUniverse().systemObjArrayKlassObj())) {
					return "* System ObjArray";
				}
				Klass bottom = oak.getBottomKlass();
				int dim = (int) oak.getDimension();
				StringBuffer buf = new StringBuffer();
				if (bottom instanceof TypeArrayKlass) {
					buf.append(((TypeArrayKlass) bottom).getElementTypeName());
				} else if (bottom instanceof InstanceKlass) {
					buf.append(bottom.getName().asString().replace('/', '.'));
				} else {
					throw new RuntimeException("should not reach here");
				}
				for (int i = 0; i < dim; i++) {
					buf.append("[]");
				}
				return buf.toString();
			}
		}
		return getInternalName(k);
	}

	public static void titleOn(PrintStream tty) {
		tty.println("Object Histogram:");
		tty.println();
		tty.println("#num" + "\t" + "#all instances/bytes" + "\t" 
				+ "#eden instances/bytes" + "\t" + "#survivor instances/bytes" + "\t" + "#old instances/bytes"
				+ "\t" + "#perm instances/bytes" + "\t"+ "#Class description");
		tty.println("-----------------------------------------------------------------------------------");
	}
	public static void titleOnOld(PrintStream tty) {
		tty.println("Old Object Histogram:");
		tty.println();
		tty.println("#num" + "\t" + "#all instances/bytes" + "\t" 
				+ "#old instances/bytes"+ "\t" + "#Class description");
		tty.println("-----------------------------------------------------------------------------------");
	}
	public void printOn(PrintStream tty) {
		tty.print(count + "/" + size + "\t");
		tty.print(edenCount + "/" + edenSize + "\t" + survivorCount + "/" + survivorSize + "\t" + oldCount + "/"
				+ oldSize + "\t" + permCount + "/" + permSize + "\t");
		tty.print(getDescription());
		tty.println();
	}
	public void printOnOld(PrintStream tty) {
		tty.print(count + "/" + size + "\t");
		tty.print(oldCount + "/" + oldSize + "\t");
		tty.print(getDescription());
		tty.println();
	}
	private static int getLocation(Oop obj) {
		if (obj == null)
			return Unknown;
		CollectedHeap heap = VM.getVM().getUniverse().heap();
		if (heap instanceof GenCollectedHeap) {
			DefNewGeneration gen0 = (DefNewGeneration) ((GenCollectedHeap) heap).getGen(0);
			if (gen0.eden().contains(obj.getHandle())) {
				return InEden;
			} else if (gen0.from().contains(obj.getHandle())) {
				return InSurvivor;
			}
			Generation gen1 = ((GenCollectedHeap) heap).getGen(1);
			if (gen1.isIn(obj.getHandle())) {
				return InOld;
			}
			Generation permGen = ((GenCollectedHeap) heap).permGen();
			if (permGen.isIn(obj.getHandle())) {
				return InPerm;
			}
		} else if (heap instanceof ParallelScavengeHeap) {
			PSYoungGen youngGen = ((ParallelScavengeHeap) heap).youngGen();
			if (youngGen.edenSpace().contains(obj.getHandle())) {
				return InEden;
			} else if (youngGen.fromSpace().contains(obj.getHandle())) {
				return InSurvivor;
			}
			PSOldGen oldGen = ((ParallelScavengeHeap) heap).oldGen();
			if (oldGen.isIn(obj.getHandle())) {
				return InOld;
			}
			PSPermGen permGen = ((ParallelScavengeHeap) heap).permGen();
			if (permGen.isIn(obj.getHandle())) {
				return InPerm;
			}
		}
		return Unknown;
	}
}
