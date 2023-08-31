package application;

import javax.persistence.*;

@Entity
@Table(name = "authors")
public class Author {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "nationality")
	private String nationality;

	@Column(name = "subject")
	private String subject;

	public Author() {
	}

	public Author(String name, String nationality, String subject) {
		this.name = name;
		this.nationality = nationality;
		this.subject = subject;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "Author ID: " + id + "\nAuthor Name: " + name + "\nAuthor Nationality: " + nationality
				+ "\nAuthor Subject: " + subject;
	}

}
