package org.commonrpc.core.codec;

import org.commonrpc.core.vo.Student2;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created by lejing on 15/11/4.
 */
public class JavaCodecTest {

	@Test
	public void test1() throws IOException, ClassNotFoundException {
		long time = System.currentTimeMillis();

		Student2 student = new Student2("zhangsan", "man", 0);
		Student2 student1 = new Student2("stu1", "man", 1);
		student.setStudent1(student1);
		student.setStudent2(student1);

		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byteArray);
		output.writeObject(student);
		output.flush();
		output.close();

		byte[] bb = byteArray.toByteArray();

		ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bb));
		Student2 s = (Student2) objectIn.readObject();
		objectIn.close();

		System.out.println(s.getName() + "," + s.getSex());
		System.out.println(s.getStudent1() + "----" + s.getStudent2());
		Assert.assertEquals(s.getStudent1(), s.getStudent2());

	}

	@Test
	public void test2() throws IOException, ClassNotFoundException {
		long time = System.currentTimeMillis();

		Student2 student = new Student2("zhangsan", "man", 0);
		Student2 student1 = new Student2("stu1", "man", 1);
		student.setStudent1(student1);
		student.setStudent2(student1);

		for (int i = 0; i < 100000; i++) {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(byteArray);
			output.writeObject(student);
			output.flush();
			output.close();

			byte[] bb = byteArray.toByteArray();

			ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bb));
			Student2 s = (Student2) objectIn.readObject();
			objectIn.close();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("time:" + time);
	}
}
