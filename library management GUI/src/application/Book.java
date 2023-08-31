package application;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {
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

	@Column(name = "pages")
	private int pages;

	@Column(name = "publisher")
	private String publisher;

	@Column(name = "publication_date")
	private LocalDate publicationDate;

	public Book() {
	}

	public Book(String title, String description, String location, BigDecimal dailyPrice, String status, int pages,
			String publisher, LocalDate publicationDate) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.dailyPrice = dailyPrice;
		this.status = status;
		this.pages = pages;
		this.publisher = publisher;
		this.publicationDate = publicationDate;
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

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public LocalDate getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	@Override
	public String toString() {
		if (code != 0) {
			return "Book Code: " + code + "\nTitle: " + title + "\nDescription: " + description + "\nLocation: "
					+ location + "\nDaily Price: " + dailyPrice + "\nStatus: " + status + "\nPages: " + pages
					+ "\nPublisher: " + publisher + "\nPublication Date: " + publicationDate;
		}
		return "";
	}
}
