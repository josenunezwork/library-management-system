package application;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
public class Loan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "loan_number", unique = true)
	private int loanNumber;

	@ManyToOne
	@JoinColumn(name = "student_id", insertable = false, updatable = false)
	private Student student;

	@Column(name = "loan_date")
	private LocalDate loanDate;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@ManyToOne
	@JoinColumn(name = "book_code", insertable = false, updatable = false)
	private Book book;

	@ManyToOne
	@JoinColumn(name = "documentary_code", insertable = false, updatable = false)
	private Documentary documentary;

	@Column(name = "item_type", columnDefinition = "VARCHAR(255) CHECK (item_type IN ('book', 'documentary'))")
	private String itemType;

	@Column(name = "daily_price", precision = 10, scale = 2)
	private BigDecimal dailyPrice;

	@Column(name = "total_price", precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(name = "student_id")
	private int studentId;

	@Column(name = "book_code")
	private Integer bookCode;

	@Column(name = "documentary_code")
	private Integer documentaryCode;

	public Loan() {
	}

	public Loan(int loanNumber, Student student, LocalDate loanDate, LocalDate dueDate, Book book,
			Documentary documentary, String itemType, BigDecimal dailyPrice, BigDecimal totalPrice,
			int documentaryCode) {
		this.loanNumber = loanNumber;
		this.student = student;
		this.loanDate = loanDate;
		this.dueDate = dueDate;
		this.book = book;
		this.documentaryCode = documentaryCode;
		this.documentary = documentary;
		this.itemType = itemType;
		this.dailyPrice = dailyPrice;
		this.totalPrice = totalPrice;
	}

	public int getId() {
		return id;
	}

	public int getLoanNumber() {
		return loanNumber;
	}

	public void setLoanNumber(int loanNumber) {
		this.loanNumber = loanNumber;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public LocalDate getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(LocalDate loanDate) {
		this.loanDate = loanDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Documentary getDocumentary() {
		return documentary;
	}

	public void setDocumentary(Documentary documentary) {
		this.documentary = documentary;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public BigDecimal getDailyPrice() {
		return dailyPrice;
	}

	public void setDailyPrice(BigDecimal dailyPrice) {
		this.dailyPrice = dailyPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public Integer getBookCode() {
		return bookCode;
	}

	public void setBookCode(Integer bookCode) {
		this.bookCode = bookCode;
	}

	public Integer getDocumentaryCode() {
		return documentaryCode;
	}

	public void setDocumentaryCode(Integer documentaryCode) {
		this.documentaryCode = documentaryCode;
	}

	@Override
	public String toString() {

		dailyPrice = itemType.equals("book") ? book.getDailyPrice() : documentary.getDailyPrice();
		// if after due date, it calculates the # of days from loan date to due date and
		// multiplies that by the daily price then
		// adds it to the # of days between the due date and now multiplied by the daily
		// price
		if (LocalDate.now().isAfter(dueDate)) {
			totalPrice = itemType.equals("book")
					? book.getDailyPrice().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(loanDate, dueDate)))
							.add(book.getDailyPrice().multiply(new BigDecimal("1.1"))
									.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(dueDate, LocalDate.now()))))

					: documentary.getDailyPrice()
							.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(loanDate, dueDate)))
							.add(documentary.getDailyPrice().multiply(new BigDecimal("1.1"))
									.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(dueDate, LocalDate.now()))));

		}
		// gets the cost from loan date to current date
		else {
			totalPrice = itemType.equals("book")
					? book.getDailyPrice()
							.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(loanDate, LocalDate.now())))
					: documentary.getDailyPrice()
							.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(loanDate, LocalDate.now())));
		}

		return itemType.equals("book")
				? "Loan{" + "id=" + id + ", loanDate=" + loanDate + ", dueDate=" + dueDate + ", loanNumber="
						+ loanNumber + ", dailyPrice=" + dailyPrice + ", totalPrice=" + totalPrice + "}\nStudent{"
						+ student + "}\nBook{" + book + "}\n"
				: "Loan{" + "id=" + id + ", loanDate=" + loanDate + ", dueDate=" + dueDate + ", loanNumber="
						+ loanNumber + ", dailyPrice=" + dailyPrice + ", totalPrice=" + totalPrice + "}\nStudent{"
						+ student + "}\n" + documentary + "\n";
	}
}
