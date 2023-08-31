package application;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.format.DateTimeParseException;
import java.util.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class LibGUI extends Application {

	private SessionFactory sessionFactory;
	private Session session;
	private TextField searchBar;
	private ListView<String> base = new ListView<>();
	private ListView<Student> studentListView = new ListView();
	private ListView<Loan> loanListView = new ListView<>();
	private ListView<Author> authorListView;
	private ListView<Director> directorListView;
	private ListView<Book> bookListView;
	private ListView<Documentary> documentaryListView;
	private ObservableList<Student> studentList = FXCollections.observableArrayList();
	private ObservableList<Author> authorList = FXCollections.observableArrayList();
	private ObservableList<Director> directorList = FXCollections.observableArrayList();
	private ObservableList<Book> bookList = FXCollections.observableArrayList();
	private ObservableList<Documentary> documentaryList = FXCollections.observableArrayList();
	private ObservableList<Loan> loanList = FXCollections.observableArrayList();
	int filter = 0;
	int tempfilter = 0;

	@Override
	public void start(Stage primaryStage) {
		// connects to data base, data base is closed on program exit
		Configuration configuration = new Configuration().configure();
		sessionFactory = configuration.buildSessionFactory();
		session = sessionFactory.openSession();

		// Horizontal box for the search label and text field
		searchBar = new TextField();
		Label searchLabel = new Label("Search:");
		HBox searchBox = new HBox(10, searchLabel, searchBar);
		searchBox.setAlignment(Pos.CENTER);

		// horizontal box for the display type buttons (maybe loans can be added here or
		// made separate)
		Label sortLabel = new Label("Display type:");
		Button studentFilter = new Button("Student");
		Button authorFilter = new Button("Author");
		Button bookFilter = new Button("Book");
		Button documentaryFilter = new Button("Documentary");
		Button directorFilter = new Button("Director");
		HBox sortbymenu = new HBox(10, sortLabel, bookFilter, documentaryFilter, studentFilter, authorFilter,
				directorFilter);
		sortbymenu.setAlignment(Pos.CENTER);

		// horizontal box for the action buttons (actions don't work yet except delete)
		Label actionLabel = new Label("Actions:");
		Button create = new Button("Create");
		Button delete = new Button("Delete");
		Button update = new Button("Update");
		HBox actionmenu = new HBox(10, actionLabel, create, delete, update);
		actionmenu.setAlignment(Pos.CENTER);

		// Vertical box for the general layout of the screen
		Button manageLoans = new Button("Manage Loans");
		Label titleLabel = new Label("CPP Library");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		VBox root = new VBox(20, titleLabel, manageLoans, searchBox, sortbymenu, actionmenu, base);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(10));
		Scene scene = new Scene(root, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("CPP Library");

		primaryStage.show();

		// Horizontal box for the loan action buttons
		Label loanActionLabel = new Label("Loan Actions:");
		Button loanCreate = new Button("Create Loan");
		Button loanDelete = new Button("Delete Loan");
		Button loanUpdate = new Button("Update Loan");
		Button loanReturn = new Button("Return Item");
		HBox loanActionMenu = new HBox(10, loanActionLabel, loanCreate, loanDelete, loanUpdate, loanReturn);
		loanActionMenu.setAlignment(Pos.CENTER);
		Label loanTitle = new Label("Manage Loans");
		loanTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		Label sortbyLabel = new Label("Sort by:");
		Button allFilter = new Button("All");
		Button currentFilter = new Button("Current Loans");
		Button overdueFilter = new Button("Overdue Loans");
		Button report = new Button("Generate Financial Report");
		// Manage Loans scene creation
		Button back = new Button("Back");
		manageLoans.setOnAction(event -> {

			tempfilter = filter;
			filter = 6;
			loanList = FXCollections.observableArrayList();
			loanList.setAll(retrieveLoansFromDatabase());
			loanListView.setItems(FXCollections.observableList(retrieveLoansFromDatabase()));
			FilteredList<Loan> filteredLoanList = new FilteredList<>(loanList);

			searchBar.textProperty().addListener(
					(observable, oldValue, newValue) -> filteredLoanList.setPredicate(loan -> newValue == null
							|| newValue.trim().isEmpty() || loan.getId() == Integer.parseInt(newValue)));

			loanListView.setItems(filteredLoanList);
			HBox sortmenu = new HBox(10, sortbyLabel, allFilter, currentFilter, overdueFilter);
			sortmenu.setAlignment(Pos.CENTER);
			VBox vb = new VBox(back);
			VBox vb2 = new VBox(20, vb, loanTitle, report, searchBox, sortmenu, loanActionMenu, loanListView);
			vb2.setAlignment(Pos.TOP_CENTER);
			vb2.setPadding(new Insets(10));
			Scene manageLoansScene = new Scene(vb2, 600, 600);
			primaryStage.setScene(manageLoansScene);
			primaryStage.setTitle("Manage Loans");

		});

		back.setOnAction(event -> {
			filter = tempfilter;
			primaryStage.setTitle("CPP Library");
			root.getChildren().add(2, searchBox);
			primaryStage.setScene(scene);
		});
		loanReturn.setOnAction(event -> {
			// Get the selected loan
			Loan selectedLoan = loanListView.getSelectionModel().getSelectedItem();

			// If a loan is selected
			if (selectedLoan != null) {
				// Create a new stage for the loan details window
				Stage loanDetailsStage = new Stage();
				loanDetailsStage.setTitle("Loan Details");

				// Create a VBox layout for the loan details window
				VBox loanDetailsLayout = new VBox();
				loanDetailsLayout.setSpacing(10);
				loanDetailsLayout.setPadding(new Insets(10));

				// Create labels for the loan details
				Label loanTitleLabel = new Label("Receipt");
				Label loanLabel = new Label(selectedLoan.toString());

				// Set styles for the labels
				loanTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
				loanLabel.setStyle("-fx-font-size: 14;");

				// Create a Print button
				Button printButton = new Button("Print Receipt");
				printButton.setOnAction(printEvent -> {
					// Show a confirmation dialog before printing
					Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
					confirmationAlert.setTitle("Confirmation");
					confirmationAlert.setHeaderText("PrintReceipt");
					confirmationAlert.setContentText("Are you sure you want to print receipt?");

					// Customize the confirmation dialog buttons
					ButtonType confirmButton = new ButtonType("Print");
					ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
					confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

					// Show the confirmation dialog and handle the button actions
					Optional<ButtonType> result = confirmationAlert.showAndWait();
					if (result.isPresent() && result.get() == confirmButton) {
						// Delete the loan from the list
						if (selectedLoan.getItemType().equals("book")) {
							selectedLoan.getBook().setStatus("available");
						} else {
							selectedLoan.getDocumentary().setStatus("available");
						}
						Transaction transaction = session.beginTransaction();
						session.delete(selectedLoan);
						transaction.commit();
						loanListView.setItems(FXCollections.observableList(retrieveLoansFromDatabase()));
						loanDetailsStage.close();
					}
				});

				// Add the labels and button to the loan details layout
				loanDetailsLayout.getChildren().addAll(loanTitleLabel, loanLabel, printButton);
				Scene loanDetailsScene = new Scene(loanDetailsLayout);
				loanDetailsStage.setScene(loanDetailsScene);
				loanDetailsStage.show();
			}
		});
		// creates report generation GUI
		report.setOnAction(event -> {

			Stage reportStage = new Stage();
			reportStage.setTitle("Report");

			VBox reportLayout = new VBox();
			reportLayout.setSpacing(10);
			reportLayout.setPadding(new Insets(10));

			// Select between student and period
			Button filterByStudentButton = new Button("Filter by Student");
			Button filterByPeriodButton = new Button("Filter by Period");
			filterByStudentButton.setOnAction(filterByStudentEvent -> {
				// displays a list of students who have loans for staff to select from
				reportStage.close();
				Stage studentStage = new Stage();
				reportStage.setTitle("Report");
				Label t1 = new Label("Student Financial Report");
				t1.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
				Label search2 = new Label("Search");
				TextField searchfield = new TextField();
				CheckBox selectall = new CheckBox("Select All");
				HBox searchbar2 = new HBox(20, search2, searchfield, selectall);
				searchbar2.setAlignment(Pos.CENTER);
				ObservableList<Student> allstud = FXCollections.observableList(retrieveStudentsFromDatabase());
				ObservableList<Loan> allLoans = FXCollections.observableList(retrieveLoansFromDatabase());
				ObservableList<Student> studentsWithLoans = FXCollections.observableArrayList();
				for (Student student : allstud) {
					for (Loan loan : allLoans) {
						if (loan.getStudentId() == student.getId()) {
							studentsWithLoans.add(student);
						}
					}
				}
				ListView<Student> studentgen = new ListView<>(studentsWithLoans);
				studentgen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				Button generate = new Button("Generate");
				VBox layout = new VBox(10, t1, searchbar2, studentgen, generate);
				layout.setAlignment(Pos.CENTER);
				layout.setSpacing(10);
				layout.setPadding(new Insets(10));
				Scene studentScene = new Scene(layout, 500, 500);
				studentStage.setScene(studentScene);
				studentStage.show();

				FilteredList<Student> filteredStudentList = new FilteredList<>(studentsWithLoans);
				searchfield.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredStudentList
								.setPredicate(student -> newValue == null || newValue.trim().isEmpty()
										|| student.getName().toLowerCase().contains(newValue.toLowerCase())));

				studentgen.setItems(filteredStudentList);
				selectall.setOnAction(sevent -> {
					if (selectall.isSelected()) {
						studentgen.getSelectionModel().selectAll();
					} else {
						studentgen.getSelectionModel().clearSelection();
					}
				});
				// Clicking generate creates a new scene that displayed the selected students,
				// the Loans,
				// and the consolidated revenue based off of the loan date up until now
				generate.setOnAction(generateEvent -> {
					ObservableList<Student> selectedItems = studentgen.getSelectionModel().getSelectedItems();
					ObservableList<String> displayitem = FXCollections.observableArrayList();
					studentStage.close();
					Stage reportS = new Stage();
					reportS.setTitle("Report");
					BigDecimal total = new BigDecimal("0.00");
					for (Student student : selectedItems) {
						Query<Loan> query = session.createQuery("FROM Loan WHERE student_id = :studentId", Loan.class);
						query.setParameter("studentId", student.getId());
						List<Loan> loans = query.getResultList();
						for (Loan loan : loans) {
							if (loan.getStudentId() == student.getId()) {
								total = total.add(loan.getTotalPrice());
								displayitem.add("" + loan);
							}

						}
					}
					Label t3 = new Label("Student Financial Report");
					t3.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
					Label revenue = new Label("Consolidated Revenue: $" + total);
					ListView<String> studentgen2 = new ListView<>(displayitem);
					Button print = new Button("Print");
					VBox layout2 = new VBox(10, t3, studentgen2, revenue, print);
					layout2.setAlignment(Pos.CENTER);
					layout2.setSpacing(10);
					layout2.setPadding(new Insets(10));
					Scene fin = new Scene(layout2, 650, 500);
					reportS.setScene(fin);
					reportS.show();
					print.setOnAction(printe -> {
						reportS.close();
					});
				});
			});
			// Staff enter a period of time
			filterByPeriodButton.setOnAction(filterByPeriodEvent -> {
				reportStage.close();
				Stage studentStage = new Stage();
				reportStage.setTitle("Report");
				Label t2 = new Label("Period Financial Report");
				t2.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
				Label st = new Label("Start Period (YYYY-MM-DD):");
				Label en = new Label("End Period (YYYY-MM-DD):");
				TextField start = new TextField();
				TextField end = new TextField();
				Button generate = new Button("Generate");
				HBox hb = new HBox(10, st, start);
				HBox hb2 = new HBox(10, en, end);
				hb.setAlignment(Pos.CENTER);
				hb2.setAlignment(Pos.CENTER);
				VBox layout = new VBox(10, t2, hb, hb2, generate);
				layout.setAlignment(Pos.CENTER);
				layout.setSpacing(10);
				layout.setPadding(new Insets(10));
				Scene studentScene = new Scene(layout, 400, 200);
				studentStage.setScene(studentScene);
				studentStage.show();
				// Searches for loans that have a due date in that period
				// and displays the loans and the total accumulated cost from the loan date to
				// current time
				generate.setOnAction(generateEvent -> {
					LocalDate s = LocalDate.parse(start.getText());
					LocalDate e = LocalDate.parse(end.getText());
					ObservableList<Loan> aloans = FXCollections.observableList(retrieveLoansFromDatabase());
					ObservableList<String> displayitem = FXCollections.observableArrayList();
					BigDecimal total = new BigDecimal("0.00");
					for (Loan loan : aloans) {
						if (loan.getDueDate().isBefore(e) && loan.getDueDate().isAfter(s)) {
							total = total.add(loan.getTotalPrice());
							displayitem.add("" + loan);
						}

					}
					studentStage.close();
					Stage reportp = new Stage();
					reportp.setTitle("Report");
					Label t3 = new Label("Period Financial Report");
					Label per = new Label("From " + start.getText() + " to " + end.getText());
					t3.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
					Label revenue = new Label("Consolidated Revenue: $" + total);
					ListView<String> loangen = new ListView<>(displayitem);
					Button print = new Button("Print");
					VBox layout2 = new VBox(10, t3, per, loangen, revenue, print);
					layout2.setAlignment(Pos.CENTER);
					layout2.setSpacing(10);
					layout2.setPadding(new Insets(10));
					Scene fin = new Scene(layout2, 650, 500);
					reportp.setScene(fin);
					reportp.show();
					print.setOnAction(printe -> {
						reportp.close();
					});
				});
			});

			reportLayout.getChildren().addAll(filterByStudentButton, filterByPeriodButton);
			Scene reportScene = new Scene(reportLayout);
			reportStage.setScene(reportScene);
			reportStage.show();
		});

		// button action for the display type buttons, making them show their respective
		// types in the list view
		studentFilter.setOnAction(event -> {
			filter = 1;
			studentListView = new ListView<>();
			studentList = FXCollections.observableArrayList();
			studentList.setAll(retrieveStudentsFromDatabase());
			studentListView.setItems(studentList);
			root.getChildren().set(5, studentListView);

			FilteredList<Student> filteredStudentList = new FilteredList<>(studentList);

			// Bind the FilteredList to the textProperty of the searchField
			searchBar.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredStudentList
							.setPredicate(student -> newValue == null || newValue.trim().isEmpty()
									|| student.getName().toLowerCase().contains(newValue.toLowerCase())));

			studentListView.setItems(filteredStudentList);
		});
		authorFilter.setOnAction(event -> {
			filter = 2;
			authorListView = new ListView<>();
			authorList = FXCollections.observableArrayList();
			authorList.setAll(retrieveAuthorsFromDatabase());
			authorListView.setItems(authorList);
			root.getChildren().set(5, authorListView);

			FilteredList<Author> filteredAuthorList = new FilteredList<>(authorList);

			searchBar.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredAuthorList
							.setPredicate(author -> newValue == null || newValue.trim().isEmpty()
									|| author.getName().toLowerCase().contains(newValue.toLowerCase())));

			authorListView.setItems(filteredAuthorList);
		});
		directorFilter.setOnAction(event -> {
			filter = 3;
			directorListView = new ListView<>();
			directorList = FXCollections.observableArrayList();
			directorList.setAll(retrieveDirectorsFromDatabase());
			directorListView.setItems(directorList);
			root.getChildren().set(5, directorListView);

			FilteredList<Director> filteredDirectorList = new FilteredList<>(directorList);

			searchBar.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredDirectorList
							.setPredicate(director -> newValue == null || newValue.trim().isEmpty()
									|| director.getName().toLowerCase().contains(newValue.toLowerCase())));

			directorListView.setItems(filteredDirectorList);
		});
		bookFilter.setOnAction(event -> {
			filter = 4;
			bookListView = new ListView<>();
			bookList = FXCollections.observableArrayList();
			bookList.setAll(retrieveBooksFromDatabase());
			bookListView.setItems(bookList);
			root.getChildren().set(5, bookListView);

			FilteredList<Book> filteredBookList = new FilteredList<>(bookList);

			searchBar.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredBookList
							.setPredicate(book -> newValue == null || newValue.trim().isEmpty()
									|| book.getTitle().toLowerCase().contains(newValue.toLowerCase())));

			bookListView.setItems(filteredBookList);
		});
		documentaryFilter.setOnAction(event -> {
			filter = 5;
			documentaryListView = new ListView<>();
			documentaryList = FXCollections.observableArrayList();
			documentaryList.setAll(retrieveDocumentariesFromDatabase());
			documentaryListView.setItems(documentaryList);
			root.getChildren().set(5, documentaryListView);

			FilteredList<Documentary> filteredDocumentaryList = new FilteredList<>(documentaryList);

			searchBar.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredDocumentaryList
							.setPredicate(documentary -> newValue == null || newValue.trim().isEmpty()
									|| documentary.getTitle().toLowerCase().contains(newValue.toLowerCase())));

			documentaryListView.setItems(filteredDocumentaryList);
		});
		allFilter.setOnAction(event -> {
			filter = 6;
			loanList = FXCollections.observableArrayList();
			loanList.setAll(retrieveLoansFromDatabase());
			loanListView.setItems(loanList);

			FilteredList<Loan> filteredLoanList = new FilteredList<>(loanList);

			searchBar.textProperty().addListener(
					(observable, oldValue, newValue) -> filteredLoanList.setPredicate(loan -> newValue == null
							|| newValue.trim().isEmpty() || loan.getId() == Integer.parseInt(newValue)));

			loanListView.setItems(filteredLoanList);
		});
		currentFilter.setOnAction(event -> {
			filter = 6;
			loanList = FXCollections.observableArrayList();
			loanList.setAll(retrieveLoansFromDatabase());
			ObservableList<Loan> current = loanList.filtered(loan -> loan.getDueDate().isAfter(LocalDate.now()));
			loanListView.setItems(current);

			FilteredList<Loan> filteredLoanList = new FilteredList<>(current);

			searchBar.textProperty().addListener(
					(observable, oldValue, newValue) -> filteredLoanList.setPredicate(loan -> newValue == null
							|| newValue.trim().isEmpty() || loan.getId() == Integer.parseInt(newValue)));

			loanListView.setItems(filteredLoanList);
		});
		overdueFilter.setOnAction(event -> {
			filter = 6;
			loanList = FXCollections.observableArrayList();
			loanList.setAll(retrieveLoansFromDatabase());
			ObservableList<Loan> past = loanList.filtered(loan -> loan.getDueDate().isBefore(LocalDate.now()));
			loanListView.setItems(past);

			FilteredList<Loan> filteredLoanList = new FilteredList<>(past);

			searchBar.textProperty().addListener(
					(observable, oldValue, newValue) -> filteredLoanList.setPredicate(loan -> newValue == null
							|| newValue.trim().isEmpty() || loan.getId() == Integer.parseInt(newValue)));

			loanListView.setItems(filteredLoanList);
		});

		// after clicking on an item in the list view, clicking the delete action will
		// remove them from the data base
		delete.setOnAction(event -> {
			if (filter == 1) {
				Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();
				Transaction transaction = session.beginTransaction();
				session.delete(selectedStudent);
				transaction.commit();
				studentListView.setItems(FXCollections.observableList(retrieveStudentsFromDatabase()));
				FilteredList<Student> filteredStudentList = new FilteredList<>(
						FXCollections.observableList(retrieveStudentsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredStudentList
								.setPredicate(student -> newValue == null || newValue.trim().isEmpty()
										|| student.getName().toLowerCase().contains(newValue.toLowerCase())));

				studentListView.setItems(filteredStudentList);
			} else if (filter == 2) {
				Author selectedAuthor = authorListView.getSelectionModel().getSelectedItem();
				Transaction transaction = session.beginTransaction();
				session.delete(selectedAuthor);
				transaction.commit();
				authorListView.setItems(FXCollections.observableList(retrieveAuthorsFromDatabase()));
				FilteredList<Author> filteredAuthorList = new FilteredList<>(
						FXCollections.observableList(retrieveAuthorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredAuthorList
								.setPredicate(author -> newValue == null || newValue.trim().isEmpty()
										|| author.getName().toLowerCase().contains(newValue.toLowerCase())));

				authorListView.setItems(filteredAuthorList);
			} else if (filter == 3) {
				Director selectedDirector = directorListView.getSelectionModel().getSelectedItem();
				Transaction transaction = session.beginTransaction();
				session.delete(selectedDirector);
				transaction.commit();
				directorListView.setItems(FXCollections.observableList(retrieveDirectorsFromDatabase()));
				FilteredList<Director> filteredDirectorList = new FilteredList<>(
						FXCollections.observableList(retrieveDirectorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredDirectorList
								.setPredicate(director -> newValue == null || newValue.trim().isEmpty()
										|| director.getName().toLowerCase().contains(newValue.toLowerCase())));

				directorListView.setItems(filteredDirectorList);
			} else if (filter == 4) {
				Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
				Transaction transaction = session.beginTransaction();
				session.delete(selectedBook);
				transaction.commit();
				bookListView.setItems(FXCollections.observableList(retrieveBooksFromDatabase()));
				FilteredList<Book> filteredBookList = new FilteredList<>(
						FXCollections.observableList(retrieveBooksFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredBookList
								.setPredicate(book -> newValue == null || newValue.trim().isEmpty()
										|| book.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				bookListView.setItems(filteredBookList);
			} else if (filter == 5) {
				Documentary selectedDocumentary = documentaryListView.getSelectionModel().getSelectedItem();
				Transaction transaction = session.beginTransaction();
				session.delete(selectedDocumentary);
				transaction.commit();
				documentaryListView.setItems(FXCollections.observableList(retrieveDocumentariesFromDatabase()));
				FilteredList<Documentary> filteredDocumentaryList = new FilteredList<>(
						FXCollections.observableList(retrieveDocumentariesFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue,
								newValue) -> filteredDocumentaryList.setPredicate(documentary -> newValue == null
										|| newValue.trim().isEmpty()
										|| documentary.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				documentaryListView.setItems(filteredDocumentaryList);
			}
		});
		// Button for creating a new student, author, documentary, director, book
		create.setOnAction(event -> {
			if (filter == 1) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Student");
				dialog.setHeaderText("Enter Student Details");
				// Ask for Broncoid
				dialog.setContentText("Enter Bronco id:");
				Optional<String> brincoid = dialog.showAndWait();
				// Ask for Name
				dialog.setContentText("Enter Name:");
				Optional<String> name = dialog.showAndWait();
				// Ask for Course
				dialog.setContentText("Enter Course:");
				Optional<String> course = dialog.showAndWait();
				// If all inputs are given
				if (brincoid.isPresent() && name.isPresent() && course.isPresent()) {
					try {
						// Create a new student
						Student newStudent = new Student();
						newStudent.setBronco_id(Integer.parseInt(brincoid.get()));
						newStudent.setName(name.get());
						newStudent.setCourse(course.get());
						// Begin transaction
						Transaction transaction = session.beginTransaction();
						session.save(newStudent);
						transaction.commit();
						// Update list view
						studentListView.setItems(FXCollections.observableList(retrieveStudentsFromDatabase()));
					} catch (NumberFormatException e) {
						System.out.println("Error: Brincoid should be an integer.");
					}
				}
				FilteredList<Student> filteredStudentList = new FilteredList<>(
						FXCollections.observableList(retrieveStudentsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredStudentList
								.setPredicate(student -> newValue == null || newValue.trim().isEmpty()
										|| student.getName().toLowerCase().contains(newValue.toLowerCase())));

				studentListView.setItems(filteredStudentList);
			} else if (filter == 2) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Author");
				dialog.setHeaderText("Enter Author Details");

				dialog.setContentText("Enter Name:");
				Optional<String> name = dialog.showAndWait();

				dialog.setContentText("Enter Subject:");
				Optional<String> subject = dialog.showAndWait();

				dialog.setContentText("Enter Nationality:");
				Optional<String> nationality = dialog.showAndWait();

				if (nationality.isPresent() && name.isPresent() && subject.isPresent()) {
					try {

						Author newAuthor = new Author();
						newAuthor.setNationality(nationality.get());
						newAuthor.setName(name.get());
						newAuthor.setSubject(subject.get());

						Transaction transaction = session.beginTransaction();
						session.save(newAuthor);
						transaction.commit();

						authorListView.setItems(FXCollections.observableList(retrieveAuthorsFromDatabase()));
					} catch (NumberFormatException e) {

					}
				}
				FilteredList<Author> filteredAuthorList = new FilteredList<>(
						FXCollections.observableList(retrieveAuthorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredAuthorList
								.setPredicate(author -> newValue == null || newValue.trim().isEmpty()
										|| author.getName().toLowerCase().contains(newValue.toLowerCase())));

				authorListView.setItems(filteredAuthorList);
			} else if (filter == 3) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Director");
				dialog.setHeaderText("Enter Director Details");

				dialog.setContentText("Enter Name:");
				Optional<String> name = dialog.showAndWait();

				dialog.setContentText("Enter Style:");
				Optional<String> style = dialog.showAndWait();

				dialog.setContentText("Enter Nationality:");
				Optional<String> nationality = dialog.showAndWait();

				if (nationality.isPresent() && name.isPresent() && style.isPresent()) {
					try {

						Director newDirector = new Director();
						newDirector.setNationality(nationality.get());
						newDirector.setName(name.get());
						newDirector.setStyle(style.get());

						Transaction transaction = session.beginTransaction();
						session.save(newDirector);
						transaction.commit();

						directorListView.setItems(FXCollections.observableList(retrieveDirectorsFromDatabase()));
					} catch (NumberFormatException e) {

					}
				}
				FilteredList<Director> filteredDirectorList = new FilteredList<>(
						FXCollections.observableList(retrieveDirectorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredDirectorList
								.setPredicate(director -> newValue == null || newValue.trim().isEmpty()
										|| director.getName().toLowerCase().contains(newValue.toLowerCase())));

				directorListView.setItems(filteredDirectorList);
			} else if (filter == 4) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Book");
				dialog.setHeaderText("Enter Book Details");

				dialog.setContentText("Enter Title:");
				Optional<String> title = dialog.showAndWait();

				dialog.setContentText("Enter Description:");
				Optional<String> description = dialog.showAndWait();

				dialog.setContentText("Enter Location:");
				Optional<String> location = dialog.showAndWait();

				dialog.setContentText("Enter Daily Price:");
				Optional<String> price = dialog.showAndWait();

				dialog.setContentText("Enter # of Pages:");
				Optional<String> pages = dialog.showAndWait();

				dialog.setContentText("Enter Publisher:");
				Optional<String> publisher = dialog.showAndWait();

				dialog.setContentText("Enter Publication Date (YYYY-MM-DD):");
				Optional<String> date = dialog.showAndWait();

				if (title.isPresent() && description.isPresent() && location.isPresent() && price.isPresent()
						&& pages.isPresent() && publisher.isPresent() && date.isPresent()) {
					try {

						Book newBook = new Book();
						newBook.setTitle(title.get());
						newBook.setDescription(description.get());
						newBook.setLocation(location.get());
						newBook.setDailyPrice(new BigDecimal(price.get()));
						newBook.setPages(Integer.parseInt(pages.get()));
						newBook.setPublisher(publisher.get());
						newBook.setPublicationDate(LocalDate.parse(date.get()));
						newBook.setStatus("available");

						Transaction transaction = session.beginTransaction();
						session.save(newBook);
						transaction.commit();

						bookListView.setItems(FXCollections.observableList(retrieveBooksFromDatabase()));
					} catch (NumberFormatException e) {

					}
				}
				FilteredList<Book> filteredBookList = new FilteredList<>(
						FXCollections.observableList(retrieveBooksFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredBookList
								.setPredicate(book -> newValue == null || newValue.trim().isEmpty()
										|| book.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				bookListView.setItems(filteredBookList);
			} else if (filter == 5) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("New Documentary");
				dialog.setHeaderText("Enter Documentary Details");

				dialog.setContentText("Enter Title:");
				Optional<String> title = dialog.showAndWait();

				dialog.setContentText("Enter Description:");
				Optional<String> description = dialog.showAndWait();

				dialog.setContentText("Enter Location:");
				Optional<String> location = dialog.showAndWait();

				dialog.setContentText("Enter Daily Price:");
				Optional<String> price = dialog.showAndWait();

				dialog.setContentText("Enter Director ID:");
				Optional<String> directorID = dialog.showAndWait();

				dialog.setContentText("Enter length:");
				Optional<String> length = dialog.showAndWait();

				dialog.setContentText("Enter Release Date (YYYY-MM-DD):");
				Optional<String> date = dialog.showAndWait();
				// If all inputs are given
				if (title.isPresent() && description.isPresent() && location.isPresent() && price.isPresent()
						&& directorID.isPresent() && length.isPresent() && date.isPresent()) {
					try {

						Documentary newDocumentary = new Documentary();
						newDocumentary.setTitle(title.get());
						newDocumentary.setDescription(description.get());
						newDocumentary.setLocation(location.get());
						newDocumentary.setDailyPrice(new BigDecimal(price.get()));
						newDocumentary.setDirector(session.get(Director.class, Integer.parseInt(directorID.get())));
						newDocumentary.setLength(Integer.parseInt(length.get()));
						newDocumentary.setReleaseDate(LocalDate.parse(date.get()));
						newDocumentary.setStatus("available");

						Transaction transaction = session.beginTransaction();
						session.save(newDocumentary);
						transaction.commit();

						documentaryListView.setItems(FXCollections.observableList(retrieveDocumentariesFromDatabase()));
					} catch (NumberFormatException e) {

					}
				}
				FilteredList<Documentary> filteredDocumentaryList = new FilteredList<>(
						FXCollections.observableList(retrieveDocumentariesFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue,
								newValue) -> filteredDocumentaryList.setPredicate(documentary -> newValue == null
										|| newValue.trim().isEmpty()
										|| documentary.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				documentaryListView.setItems(filteredDocumentaryList);
			}

		});

		// Button to update student, author, documentary, book, director
		update.setOnAction(event -> {
			if (filter == 1) {
				// Get the selected student
				Student selectedStudent = studentListView.getSelectionModel().getSelectedItem();

				// If a student is selected
				if (selectedStudent != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Update Student");
					dialog.setHeaderText("Enter New Student Details");

					// Ask for new Bronco id
					dialog.setContentText("Enter new Broncoid:");
					dialog.getEditor().setText(Integer.toString(selectedStudent.getBronco_id()));
					Optional<String> broncoid = dialog.showAndWait();

					// Ask for new Name
					dialog.setContentText("Enter new Name:");
					dialog.getEditor().setText(selectedStudent.getName());
					Optional<String> name = dialog.showAndWait();

					// Ask for new Course
					dialog.setContentText("Enter new Course:");
					dialog.getEditor().setText(selectedStudent.getCourse());
					Optional<String> course = dialog.showAndWait();

					// If all inputs are given
					if (broncoid.isPresent() && name.isPresent() && course.isPresent()) {
						try {
							// Begin transaction
							Transaction transaction = session.beginTransaction();

							// Update the student object
							selectedStudent.setBronco_id(Integer.parseInt(broncoid.get()));
							selectedStudent.setName(name.get());
							selectedStudent.setCourse(course.get());

							// Update the student in the database
							session.update(selectedStudent);

							// Commit transaction
							transaction.commit();

							// Update list view
							studentListView.setItems(FXCollections.observableList(retrieveStudentsFromDatabase()));
						} catch (NumberFormatException e) {
							System.out.println("Error: Broncoid should be an integer.");
						}
					}
				} else {
					// Show message if no student is selected
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("No Selection");
					alert.setHeaderText(null);
					alert.setContentText("Please select a student to update.");
					alert.showAndWait();
				}
				FilteredList<Student> filteredStudentList = new FilteredList<>(
						FXCollections.observableList(retrieveStudentsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredStudentList
								.setPredicate(student -> newValue == null || newValue.trim().isEmpty()
										|| student.getName().toLowerCase().contains(newValue.toLowerCase())));

				studentListView.setItems(filteredStudentList);
			} else if (filter == 2) {

				Author selectedAuthor = authorListView.getSelectionModel().getSelectedItem();

				if (selectedAuthor != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Update Author");
					dialog.setHeaderText("Enter New Author Details");

					dialog.setContentText("Enter new nationality:");
					dialog.getEditor().setText(selectedAuthor.getNationality());
					Optional<String> nationality = dialog.showAndWait();

					dialog.setContentText("Enter new Name:");
					dialog.getEditor().setText(selectedAuthor.getName());
					Optional<String> name = dialog.showAndWait();

					dialog.setContentText("Enter new Subject:");
					dialog.getEditor().setText(selectedAuthor.getSubject());
					Optional<String> subject = dialog.showAndWait();

					if (nationality.isPresent() && name.isPresent() && subject.isPresent()) {
						try {

							Transaction transaction = session.beginTransaction();

							selectedAuthor.setNationality(nationality.get());
							selectedAuthor.setName(name.get());
							selectedAuthor.setSubject(subject.get());

							session.update(selectedAuthor);

							transaction.commit();

							authorListView.setItems(FXCollections.observableList(retrieveAuthorsFromDatabase()));
						} catch (NumberFormatException e) {

						}
					}
				} else {
					// Show message if no student is selected
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("No Selection");
					alert.setHeaderText(null);
					alert.setContentText("Please select a Author to update.");
					alert.showAndWait();
				}
				FilteredList<Author> filteredAuthorList = new FilteredList<>(
						FXCollections.observableList(retrieveAuthorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredAuthorList
								.setPredicate(author -> newValue == null || newValue.trim().isEmpty()
										|| author.getName().toLowerCase().contains(newValue.toLowerCase())));

				authorListView.setItems(filteredAuthorList);
			} else if (filter == 3) {
				Director selectedDirector = directorListView.getSelectionModel().getSelectedItem();

				if (selectedDirector != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Update Director");
					dialog.setHeaderText("Enter New Director Details");

					dialog.setContentText("Enter new nationality:");
					dialog.getEditor().setText(selectedDirector.getNationality());
					Optional<String> nationality = dialog.showAndWait();

					dialog.setContentText("Enter new Name:");
					dialog.getEditor().setText(selectedDirector.getName());
					Optional<String> name = dialog.showAndWait();

					dialog.setContentText("Enter new Subject:");
					dialog.getEditor().setText(selectedDirector.getStyle());
					Optional<String> style = dialog.showAndWait();

					if (nationality.isPresent() && name.isPresent() && style.isPresent()) {
						try {

							Transaction transaction = session.beginTransaction();

							selectedDirector.setNationality(nationality.get());
							selectedDirector.setName(name.get());
							selectedDirector.setStyle(style.get());

							session.update(selectedDirector);

							transaction.commit();

							directorListView.setItems(FXCollections.observableList(retrieveDirectorsFromDatabase()));
						} catch (NumberFormatException e) {

						}
					}
				} else {

					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("No Selection");
					alert.setHeaderText(null);
					alert.setContentText("Please select a Director to update.");
					alert.showAndWait();
				}
				FilteredList<Director> filteredDirectorList = new FilteredList<>(
						FXCollections.observableList(retrieveDirectorsFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredDirectorList
								.setPredicate(director -> newValue == null || newValue.trim().isEmpty()
										|| director.getName().toLowerCase().contains(newValue.toLowerCase())));

				directorListView.setItems(filteredDirectorList);
			} else if (filter == 4) {
				Book selectedBook = bookListView.getSelectionModel().getSelectedItem();

				if (selectedBook != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Update Book");
					dialog.setHeaderText("Enter New Book Details");

					dialog.setContentText("Enter Title:");
					dialog.getEditor().setText(selectedBook.getTitle());
					Optional<String> title = dialog.showAndWait();

					dialog.setContentText("Enter Description:");
					dialog.getEditor().setText(selectedBook.getDescription());
					Optional<String> description = dialog.showAndWait();

					dialog.setContentText("Enter Location:");
					dialog.getEditor().setText(selectedBook.getLocation());
					Optional<String> location = dialog.showAndWait();

					dialog.setContentText("Enter Daily Price:");
					dialog.getEditor().setText(selectedBook.getDailyPrice().toString());
					Optional<String> price = dialog.showAndWait();

					dialog.setContentText("Enter # of Pages:");
					dialog.getEditor().setText(Integer.toString(selectedBook.getPages()));
					Optional<String> pages = dialog.showAndWait();

					dialog.setContentText("Enter Publisher:");
					dialog.getEditor().setText(selectedBook.getPublisher());
					Optional<String> publisher = dialog.showAndWait();

					dialog.setContentText("Enter Publication Date (YYYY-MM-DD):");
					dialog.getEditor().setText(selectedBook.getPublicationDate().toString());
					Optional<String> date = dialog.showAndWait();
					// If all inputs are given
					if (title.isPresent() && description.isPresent() && location.isPresent() && price.isPresent()
							&& pages.isPresent() && publisher.isPresent() && date.isPresent()) {
						try {
							Transaction transaction = session.beginTransaction();

							selectedBook.setTitle(title.get());
							selectedBook.setDescription(description.get());
							selectedBook.setLocation(location.get());
							selectedBook.setDailyPrice(new BigDecimal(price.get()));
							selectedBook.setPages(Integer.parseInt(pages.get()));
							selectedBook.setPublisher(publisher.get());
							selectedBook.setPublicationDate(LocalDate.parse(date.get()));

							session.update(selectedBook);

							transaction.commit();

							bookListView.setItems(FXCollections.observableList(retrieveBooksFromDatabase()));
						} catch (NumberFormatException e) {

						}
					}
				} else {

					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("No Selection");
					alert.setHeaderText(null);
					alert.setContentText("Please select a Book to update.");
					alert.showAndWait();
				}
				FilteredList<Book> filteredBookList = new FilteredList<>(
						FXCollections.observableList(retrieveBooksFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue, newValue) -> filteredBookList
								.setPredicate(book -> newValue == null || newValue.trim().isEmpty()
										|| book.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				bookListView.setItems(filteredBookList);
			} else if (filter == 5) {
				Documentary selectedDocumentary = documentaryListView.getSelectionModel().getSelectedItem();

				if (selectedDocumentary != null) {
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Update Documentary");
					dialog.setHeaderText("Enter New Documentary Details");

					dialog.setContentText("Enter Title:");
					dialog.getEditor().setText(selectedDocumentary.getTitle());
					Optional<String> title = dialog.showAndWait();

					dialog.setContentText("Enter Description:");
					dialog.getEditor().setText(selectedDocumentary.getDescription());
					Optional<String> description = dialog.showAndWait();

					dialog.setContentText("Enter Location:");
					dialog.getEditor().setText(selectedDocumentary.getLocation());
					Optional<String> location = dialog.showAndWait();

					dialog.setContentText("Enter Daily Price:");
					dialog.getEditor().setText(selectedDocumentary.getDailyPrice().toString());
					Optional<String> price = dialog.showAndWait();

					dialog.setContentText("Enter Director ID:");
					dialog.getEditor().setText(Integer.toString(selectedDocumentary.getDirector().getId()));
					Optional<String> directorID = dialog.showAndWait();

					dialog.setContentText("Enter Length:");
					dialog.getEditor().setText(Integer.toString(selectedDocumentary.getLength()));
					Optional<String> length = dialog.showAndWait();

					dialog.setContentText("Enter Release Date (YYYY-MM-DD):");
					dialog.getEditor().setText(selectedDocumentary.getReleaseDate().toString());
					Optional<String> date = dialog.showAndWait();
					// If all inputs are given
					if (title.isPresent() && description.isPresent() && location.isPresent() && price.isPresent()
							&& directorID.isPresent() && length.isPresent() && date.isPresent()) {
						try {
							Transaction transaction = session.beginTransaction();

							selectedDocumentary.setTitle(title.get());
							selectedDocumentary.setDescription(description.get());
							selectedDocumentary.setLocation(location.get());
							selectedDocumentary.setDailyPrice(new BigDecimal(price.get()));
							selectedDocumentary
									.setDirector(session.get(Director.class, Integer.parseInt(directorID.get())));
							selectedDocumentary.setLength(Integer.parseInt(length.get()));
							selectedDocumentary.setReleaseDate(LocalDate.parse(date.get()));

							session.update(selectedDocumentary);

							transaction.commit();

							documentaryListView
									.setItems(FXCollections.observableList(retrieveDocumentariesFromDatabase()));
						} catch (NumberFormatException e) {

						}
					}
				} else {

					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("No Selection");
					alert.setHeaderText(null);
					alert.setContentText("Please select a Documentary to update.");
					alert.showAndWait();
				}
				FilteredList<Documentary> filteredDocumentaryList = new FilteredList<>(
						FXCollections.observableList(retrieveDocumentariesFromDatabase()));

				searchBar.textProperty()
						.addListener((observable, oldValue,
								newValue) -> filteredDocumentaryList.setPredicate(documentary -> newValue == null
										|| newValue.trim().isEmpty()
										|| documentary.getTitle().toLowerCase().contains(newValue.toLowerCase())));

				documentaryListView.setItems(filteredDocumentaryList);
			}
		});
		// Button to create loan
		loanCreate.setOnAction(event -> {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("New Loan");
			dialog.setHeaderText("Enter Loan Details");

			// Ask for Loan Number
			dialog.setContentText("Enter Loan Number:");
			Optional<String> loanNumber = dialog.showAndWait();

			// Ask for Student ID
			dialog.setContentText("Enter Student ID:");
			Optional<String> studentId = dialog.showAndWait();

			// Ask for Loan Date
			dialog.setContentText("Enter Loan Date (YYYY-MM-DD):");
			Optional<String> loanDate = dialog.showAndWait();

			// Ask for Due Date
			dialog.setContentText("Enter Due Date (YYYY-MM-DD):");
			Optional<String> dueDate = dialog.showAndWait();

			// Ask for Book Code
			dialog.setContentText("Enter Book Code (or leave blank if not applicable):");
			Optional<String> bookCode = dialog.showAndWait();

			// Ask for Documentary Code
			dialog.setContentText("Enter Documentary Code (or leave blank if not applicable):");
			Optional<String> documentaryCode = dialog.showAndWait();

			// If all inputs are given
			if (loanNumber.isPresent() && studentId.isPresent() && loanDate.isPresent() && dueDate.isPresent()) {
				try {
					int loanNumberValue = Integer.parseInt(loanNumber.get());
					int studentIdValue = Integer.parseInt(studentId.get());

					// Validate loan number and student ID as integers
					if (loanNumberValue <= 0 || studentIdValue <= 0) {
						System.out.println("Error: Loan Number and Student ID should be positive integers.");
						return;
					}

					// Create a new loan
					Loan newLoan = new Loan();
					newLoan.setLoanNumber(loanNumberValue);
					Query query = session.createQuery("from Student where bronco_id = :bronco_id");
					query.setParameter("bronco_id", studentIdValue);
					Student stemp = (Student) query.uniqueResult();
					newLoan.setStudent(stemp);
					newLoan.setStudentId(stemp.getId());
					newLoan.setLoanDate(LocalDate.parse(loanDate.get()));
					newLoan.setDueDate(LocalDate.parse(dueDate.get()));

					// Set the book code and documentary code if they were provided
					if (bookCode.isPresent() && !bookCode.get().isEmpty() && Integer.parseInt(bookCode.get()) != 0) {
						System.out.println("test");
						int bookCodeValue = Integer.parseInt(bookCode.get());
						Book temp = session.get(Book.class, bookCodeValue);
						session.get(Book.class, Integer.parseInt(bookCode.get())).setStatus("borrowed");
						newLoan.setBookCode(bookCodeValue);
						newLoan.setItemType("book");
						newLoan.setBook(temp);
						newLoan.setDocumentaryCode(null);
					} else if (documentaryCode.isPresent() && !documentaryCode.get().isEmpty()
							&& Integer.parseInt(documentaryCode.get()) != 0) {
						System.out.println("test22222");
						int documentaryCodeValue = Integer.parseInt(documentaryCode.get());
						Documentary temp = session.get(Documentary.class, documentaryCodeValue);
						session.get(Documentary.class, Integer.parseInt(documentaryCode.get())).setStatus("borrowed");
						newLoan.setDocumentaryCode(documentaryCodeValue);
						newLoan.setItemType("documentary");
						newLoan.setDocumentary(temp);
						newLoan.setBookCode(null);
					}

					// Begin transaction
					Transaction transaction = session.getTransaction();
					if (!transaction.isActive()) {
						transaction.begin();
					}
					session.save(newLoan);
					transaction.commit();

					// Update list view
					loanListView.setItems(FXCollections.observableList(retrieveLoansFromDatabase()));
				} catch (NumberFormatException e) {
					System.out.println(
							"Error: Loan Number, Student ID, Book Code, and Documentary Code should be integers.");
				} catch (DateTimeParseException e) {
					System.out.println("Error: Loan Date and Due Date should be valid dates.");
				}
			}

		});
		// Button to delete loan
		loanDelete.setOnAction(event -> {
			Loan selectedLoan = loanListView.getSelectionModel().getSelectedItem();
			if (selectedLoan.getItemType().equals("book")) {
				selectedLoan.getBook().setStatus("available");
			} else {
				selectedLoan.getDocumentary().setStatus("available");
			}
			Transaction transaction = session.beginTransaction();
			session.delete(selectedLoan);
			transaction.commit();
			loanListView.setItems(FXCollections.observableList(retrieveLoansFromDatabase()));
		});
		// Button to update loan for loan extension
		loanUpdate.setOnAction(event -> {
			// Ensure a loan is selected
			Loan selectedLoan = loanListView.getSelectionModel().getSelectedItem();
			if (selectedLoan == null) {
				System.out.println("No loan selected.");
				return;
			}

			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Update Loan");
			dialog.setHeaderText("Enter Updated Loan Details");

			// Load the current loan details into the dialog
			dialog.setContentText("Enter Loan Number:");
			dialog.getEditor().setText(String.valueOf(selectedLoan.getLoanNumber()));
			Optional<String> loanNumber = dialog.showAndWait();

			dialog.setContentText("Enter Student ID:");
			dialog.getEditor()
					.setText(String.valueOf(session.get(Student.class, selectedLoan.getStudentId()).getBronco_id()));
			Optional<String> studentId = dialog.showAndWait();

			dialog.setContentText("Enter Loan Date (YYYY-MM-DD):");
			dialog.getEditor().setText(selectedLoan.getLoanDate().toString());
			Optional<String> loanDate = dialog.showAndWait();

			dialog.setContentText("Enter Due Date (YYYY-MM-DD):");
			dialog.getEditor().setText(selectedLoan.getDueDate().toString());
			Optional<String> dueDate = dialog.showAndWait();

			// If all inputs are given
			if (loanNumber.isPresent() && studentId.isPresent() && loanDate.isPresent() && dueDate.isPresent()) {
				try {
					// Parse and validate the new values
					// This is similar to the "New Loan" handler

					// Begin transaction
					Transaction transaction = session.beginTransaction();

					// Set the new values
					Query query = session.createQuery("from Student where bronco_id = :bronco_id");
					query.setParameter("bronco_id", Integer.parseInt(studentId.get()));
					Student stemp = (Student) query.uniqueResult();
					selectedLoan.setStudent(stemp);
					selectedLoan.setStudentId(stemp.getId());
					selectedLoan.setLoanNumber(Integer.parseInt(loanNumber.get()));
					selectedLoan.setLoanDate(LocalDate.parse(loanDate.get()));
					selectedLoan.setDueDate(LocalDate.parse(dueDate.get()));

					// Update the book code and documentary code if they were provided
					// This is similar to the "New Loan" handler

					// Save the changes
					session.update(selectedLoan);
					transaction.commit();

					// Update list view
					loanListView.setItems(FXCollections.observableList(retrieveLoansFromDatabase()));
				} catch (NumberFormatException e) {
					System.out.println(
							"Error: Loan Number, Student ID, Book Code, and Documentary Code should be integers.");
				} catch (DateTimeParseException e) {
					System.out.println("Error: Loan Date and Due Date should be valid dates.");
				}
			}
		});
	}

	// closes database session on closure of the program
	@Override
	public void stop() throws Exception {
		session.close();
		sessionFactory.close();
	}

	// Several retrieval methods for Authors. Students, Documentaries, Directors,
	// and Books from the database
	private List<Author> retrieveAuthorsFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Author> query = builder.createQuery(Author.class);
			Root<Author> root = query.from(Author.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	private List<Director> retrieveDirectorsFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Director> query = builder.createQuery(Director.class);
			Root<Director> root = query.from(Director.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	private List<Student> retrieveStudentsFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Student> query = builder.createQuery(Student.class);
			Root<Student> root = query.from(Student.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	private List<Book> retrieveBooksFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Book> query = builder.createQuery(Book.class);
			Root<Book> root = query.from(Book.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	private List<Documentary> retrieveDocumentariesFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Documentary> query = builder.createQuery(Documentary.class);
			Root<Documentary> root = query.from(Documentary.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	private List<Loan> retrieveLoansFromDatabase() {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Loan> query = builder.createQuery(Loan.class);
			Root<Loan> root = query.from(Loan.class);
			query.select(root);

			return session.createQuery(query).list();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<>(); // Return an empty list if retrieval fails
	}

	public static void main(String[] args) {
		launch(args);
	}
}
