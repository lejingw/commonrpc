package org.commonrpc.core.vo;

import java.io.Serializable;

/**
 * Created by lejing on 15/11/4.
 */
public class Student2 implements Serializable {
	private String name;
	private String sex;
	private int age;
	private Student2 student1;
	private Student2 student2;

	public Student2() {
	}

	public Student2(String name, String sex, int age) {
		this.name = name;
		this.sex = sex;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Student2 getStudent1() {
		return student1;
	}

	public void setStudent1(Student2 student1) {
		this.student1 = student1;
	}

	public Student2 getStudent2() {
		return student2;
	}

	public void setStudent2(Student2 student2) {
		this.student2 = student2;
	}
}
