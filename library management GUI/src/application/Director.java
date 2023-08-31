package application;

import javax.persistence.*;

@Entity
@Table(name = "directors")
public class Director {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "nationality")
	private String nationality;

	@Column(name = "style")
	private String style;

	public Director() {
	}

	public Director(String name, String nationality, String style) {
		this.name = name;
		this.nationality = nationality;
		this.style = style;
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

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String toString() {
		return "Director{" + "id=" + id + ", name='" + name + '\'' + ", nationality='" + nationality + '\''
				+ ", style='" + style + '\'' + '}';
	}
}
