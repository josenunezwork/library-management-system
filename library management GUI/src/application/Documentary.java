package application;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "documentaries")
public class Documentary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code")
	private int code;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "location")
	private String location;

	@Column(name = "daily_price", precision = 10, scale = 2)
	private BigDecimal dailyPrice;

	@Column(name = "status", columnDefinition = "VARCHAR(255) CHECK (status IN ('borrowed', 'available'))")
	private String status;

	@ManyToOne
	@JoinColumn(name = "director_id")
	private Director director;

	@Column(name = "length")
	private int length;

	@Column(name = "release_date")
	private LocalDate releaseDate;

	public Documentary() {
	}

	public Documentary(String title, String description, String location, BigDecimal dailyPrice, String status,
			Director director, int length, LocalDate releaseDate) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.dailyPrice = dailyPrice;
		this.status = status;
		this.director = director;
		this.length = length;
		this.releaseDate = releaseDate;
	}

	public int getCode() {
		return code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BigDecimal getDailyPrice() {
		return dailyPrice;
	}

	public void setDailyPrice(BigDecimal dailyPrice) {
		this.dailyPrice = dailyPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Director getDirector() {
		return director;
	}

	public void setDirector(Director director) {
		this.director = director;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public String toString() {
		if (code != 0) {
			return "Documentary{" + "code=" + code + ", title='" + title + '\'' + ", \ndescription='" + description
					+ '\'' + ", \nlocation='" + location + '\'' + ", dailyPrice=" + dailyPrice + ", status='" + status
					+ '\'' + ", \ndirector=" + director + ", \nlength=" + length + ", releaseDate=" + releaseDate + '}';
		}
		return "";
	}
}
