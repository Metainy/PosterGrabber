package ui;

import api.MovieAPIImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Movie;
import util.Constants;
import util.ImageViewPane;
import util.KeyValuePair;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Controller implements Initializable {

    private Stage primaryStage;
    private Preferences preferences;
    private Movie movie;

    @FXML
    private TableView<Movie> movieTable;
    @FXML
    private TableColumn<Movie, String> titleColumn;
    @FXML
    private TableColumn<Movie, String> yearColumn;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<KeyValuePair> cbPosterQuality;
    @FXML
    private ImageView imgPoster;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblOverview;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblImagePlaceHolder;
    @FXML
    private CheckMenuItem mItemSaveDialog;
    @FXML
    private ProgressIndicator imageProgressIndicator;
    @FXML
    private ProgressIndicator resultsProgressIndicator;
    private ImageViewPane imageViewPane;

    private String userDirectoryString;
    private boolean isMenuItemChecked;

    @FXML
    private StackPane stackPane;

    public void initialize(URL location, ResourceBundle resources) {

        // Create preference file if it doesn't exist already
        preferences = Preferences.userNodeForPackage(Main.class);

        // Get CheckBox menu item last state from preferences file, and set the retrieved state
        isMenuItemChecked = preferences.getBoolean("menuCheckState", false);
        mItemSaveDialog.setSelected(isMenuItemChecked);

        // Get last save location from preferences
        userDirectoryString = preferences.get("saveLocation", "empty");

        // Adding a custom view
        imageViewPane = new ImageViewPane();
        stackPane.getChildren().add(imageViewPane);

        // Populating media quality ComboBox
        populateQualityCombo();

        // Set default empty TableView message
        Label lblPlaceHolder = new Label("No results to display");
        lblPlaceHolder.setOpacity(0.40);
        movieTable.setPlaceholder(lblPlaceHolder);

        // Setting 'Don't Show Save Dialog' MenuItem listener
        mItemSaveDialog.selectedProperty().addListener((observable3, oldValue3, newValue3) -> {

            // Update Checkbox state in preferences file, and local variable
            preferences.putBoolean("menuCheckState", newValue3);
            isMenuItemChecked = newValue3;
        });

        // Media quality ComboBox listener
        cbPosterQuality.valueProperty().addListener((observable1, oldValue1, newValue1) -> {

            // Save selected index to preferences file
            preferences.putInt("mediaQuality", cbPosterQuality.getSelectionModel().getSelectedIndex());

            // Display the newly selected quality according to user's changes
            if (movie != null) {

                // Re-download the image on request
                downloadPoster();
            }
        });

        // Movies table listener
        movieTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {

            // Hide place holder
            lblImagePlaceHolder.setVisible(false);

            // Pass the movie's information
            this.movie = newValue;

            // Display movie's details
            showMovieDetails();
        }));
    }

    /**
     * Display downloaded data into views, corresponding with the selected movie
     */
    private void showMovieDetails() {

        if (movie != null) {

            // Display movie title, year, and overview into corresponding labels
            if (movie.getReleaseDate().equals("")) {

                lblTitle.setText(movie.getMovieTitle());
                lblOverview.setText(movie.getMovieOverview());
            } else {

                lblTitle.setText(String.format("%s (%s)", movie.getMovieTitle(), movie.getReleaseDate()));
                lblOverview.setText(movie.getMovieOverview());
            }

            // Display movie media into an image view
            downloadPoster();
        }
    }

    /**
     * Downloads an image and display it on an ImageView
     */
    private void downloadPoster() {

        try {
            // Downloading the image from URL
            URL url = new URL(Constants.MOVIE_MEDIA_URL + cbPosterQuality.getSelectionModel().getSelectedItem().getKey() + movie.getPosterURL());
            Image image = new Image(url.toExternalForm(), true);

            // Adding the image to the ImageView
            imgPoster.setImage(image);

            // Adding the ImageView to the custom made ImageViewPane
            imageViewPane.setImageView(imgPoster);

            // Binding the image download progress to the progress indicator
            imageProgressIndicator.visibleProperty().bind(image.progressProperty().lessThan(1));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display a file chooser save dialog and saves the selected media
     */
    private void displayFileChooser() {

        // Initiate file chooser, and set title
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Poster");

        // Set extension filter
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);

        // If preferences file is empty
        if (userDirectoryString.equals("empty")) {

            userDirectoryString = System.getProperty("user.home");
        }

        File userDirectory = new File(userDirectoryString);

        // If it can't access User Home, default to C drive
        if (!userDirectory.canRead()) {
            userDirectory = new File("C:\\");
        }

        // Set the initial save directory
        fileChooser.setInitialDirectory(userDirectory);

        if (movie != null) {

            // Get movie's title and set an initial file name
            String movieTitle = movie.getMovieTitle();

            // Replace any invalid characters
            movieTitle = movieTitle.replaceAll("[\"/?\"*><|]", "").replace(":", "-");

            // Set initial file name
            fileChooser.setInitialFileName(movieTitle);
        }

        // Show file chooser dialog
        File file = fileChooser.showSaveDialog(primaryStage);

        // Check file isn't null, so it the image view
        if (file != null && imgPoster.getImage() != null) {

            try {

                ImageIO.write(SwingFXUtils.fromFXImage(imgPoster.getImage(), null), "jpg", file);
                setLblStatus("Poster saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Update preferences with the new location, and local variable
            preferences.put("saveLocation", file.getParentFile().toString());
            userDirectoryString = file.getParentFile().toString();
        }
    }

    /**
     * Saves the selected poster instantly without displaying a file chooser dialog
     */
    private void saveImageInstantly() {

        if (movie != null) {

            // Get movie's title and set an initial file name
            String movieTitle = movie.getMovieTitle();

            // Replace any invalid characters
            movieTitle = movieTitle.replaceAll("[\"/?\"*><|]", "").replace(":", "-");

            // If preferences file is empty
            if (userDirectoryString.equals("empty")) {

                userDirectoryString = System.getProperty("user.home");
            }

            File userDirectory = new File(userDirectoryString + "/" + movieTitle + ".jpg");

            // If it can't access User Home, default to C drive
            if (!userDirectory.getParentFile().canRead()) {
                userDirectory = new File("D:\\" + movieTitle + ".jpg");
            }

            // Check file isn't null, so it the image view
            if (imgPoster.getImage() != null) {

                try {

                    ImageIO.write(SwingFXUtils.fromFXImage(imgPoster.getImage(), null), "jpg", userDirectory);
                    setLblStatus("Poster saved successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Populate image quality ComboBox with initial data
     */
    public void populateQualityCombo() {

        // Making a list of poster qualities
        List<KeyValuePair> qualityList = new ArrayList<KeyValuePair>() {{

            add(new KeyValuePair(Constants.MEDIA_QUALITY_w92, "Thumbnail\t[ 92x138 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_w154, "Tiny\t\t\t[ 154x231 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_w185, "Small\t\t[ 185x278 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_w342, "Medium\t\t[ 342x513 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_w500, "Large\t\t[ 500x750 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_w780, "xLarge\t\t[ 780x1170 ]"));
            add(new KeyValuePair(Constants.MEDIA_QUALITY_original, "xxLarge\t\t[ HD ]"));
        }};

        // Converting the list to an observable list
        ObservableList<KeyValuePair> obList = FXCollections.observableList(qualityList);

        // Filling the ComboBox
        cbPosterQuality.setItems(obList);

        // Setting the default value for the ComboBox
        cbPosterQuality.getSelectionModel().select(preferences.getInt("mediaQuality", 3));
    }

    /**
     * Onclick handler for the search button
     */
    @FXML
    private void btnSearchClick() {

        // Check if search field isn't empty
        if (!txtSearch.getText().equals("")) {

            // Display the progress indicator
            resultsProgressIndicator.setVisible(true);

            // Get query results
            MovieAPIImpl movieAPIImpl = new MovieAPIImpl();
            movieAPIImpl.getMovieList(Constants.SERVICE_API_KEY, txtSearch.getText(), this);
        }
    }

    /**
     * Onclick handler for download button
     */
    @FXML
    private void btnDownloadClick() {

        // Check the menu item to see if it should display a file choose or download the poster instantly
        if (isMenuItemChecked) {
            saveImageInstantly();
        } else {
            displayFileChooser();
        }
    }

    /**
     * onClick handler for MenuItem 'About'
     */
    @FXML
    private void aboutMenuItem() {

        // Creating an alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Poster Grabber 1.0");
        alert.setContentText("Poster Grabber is a very simple tool that grabs movie's poster in any desired quality.\nIt's highly dependant on TheMovieDB API, and won't function without an internet connection.");
        alert.initOwner(primaryStage);

        // Setting custom style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/AboutDialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");

        // Displaying the alert dialog
        alert.show();
    }

    /**
     * onClick handler for MenuItem 'Exit'
     */
    @FXML
    private void exitMenuItem() {

        // Exit the entire application
        Platform.exit();
    }

    /**
     * onClick handler for MenuItem 'Reset Results'
     */
    @FXML
    private void resetMenuItem() {

        // Clear old data
        movie = null;
        movieTable.getItems().clear();
        txtSearch.clear();
        lblOverview.setText("");
        lblTitle.setText("");
        imgPoster.setImage(null);
        lblImagePlaceHolder.setVisible(true);
        setLblStatus("Waiting");
    }

    /**
     * Invoked when search results fetched successfully
     *
     * @param movieList
     */
    public void onMoviesReceive(ObservableList<Movie> movieList) {

        // Set table's data source
        movieTable.setItems(movieList);

        // Initialize columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));

        // Hide progress indicator
        resultsProgressIndicator.setVisible(false);

        Platform.runLater(() -> {

            setLblStatus("Search results fetched successfully.");
        });
    }

    /**
     * Invoked when an error occurs when fetching results
     */
    public void onMoviesError() {

        // Hide progress indicator
        resultsProgressIndicator.setVisible(false);

        Platform.runLater(() -> {

            // Creating an alert dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Connectivity Error");
            alert.setContentText("Poster Grabber could not connect. Your connection might be down for the moment or the host is down.\nPlease check your internet connection and try again later.");
            alert.initOwner(primaryStage);

            // Setting custom style
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/ErrorDialog.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");

            // Displaying the alert dialog
            alert.show();
        });

    }

    public void setPrimaryStage(Stage primaryStage) {

        this.primaryStage = primaryStage;
    }

    public void setLblStatus(String text) {

        this.lblStatus.setText(text);
    }
}