package application;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Student {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "bronco_id")
	private int bronco_id;

	@Column(name = "name")
	private String name;

	@Column(name = "course")
	private String course;

	public Student() {

	}

	public int getId() {
		return id;
	}

	public int getBronco_id() {
		return bronco_id;
	}

	public void setBronco_id(int bronco_id) {
		this.bronco_id = bronco_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return "Bronco ID: " + bronco_id + ", Student Name: " + name + ", Student Course: " + course;
	}
}
