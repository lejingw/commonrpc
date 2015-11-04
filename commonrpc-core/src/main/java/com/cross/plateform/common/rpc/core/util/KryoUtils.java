package com.cross.plateform.common.rpc.core.util;

import com.esotericsoftware.kryo.Kryo;

public class KryoUtils {
//	private static final List<Class> classList = new ArrayList<Class>();
//	private static final List<Serializer> serializerList = new ArrayList<Serializer>();
//	private static final List<Integer> idList = new ArrayList<Integer>();

	private KryoUtils() {
	}

	private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
//			int size = idList.size();
//			for (int i = 0; i < size; i++) {
//				kryo.register(classList.get(i), serializerList.get(i), idList.get(i));
//			}
//			kryo.setRegistrationRequired(true);
			kryo.setReferences(false);
			return kryo;
		}
	};

//	/**
//	 * @param className
//	 * @param serializer
//	 * @param id
//	 */
//	public static synchronized void registerClass(Class className, Serializer serializer, int id) {
//		classList.add(className);
//		serializerList.add(serializer);
//		idList.add(id);
//	}

	/**
	 * @return
	 */
	public static Kryo getKryo() {
		return kryos.get();
	}
}
