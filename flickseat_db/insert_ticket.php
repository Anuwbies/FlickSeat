<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");

require_once "db_config.php"; // Include database configuration

// Check if request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Retrieve POST data
    $user_id = $_POST['user_id'] ?? null;
    $movie_id = $_POST['movie_id'] ?? null;
    $showtime_id = $_POST['showtime_id'] ?? null;
    $seat_id = $_POST['seat_id'] ?? null;
    $ticket_price = $_POST['ticket_price'] ?? null;

    // Validate input
    if (!$user_id || !$movie_id || !$showtime_id || !$seat_id || !$ticket_price) {
        echo json_encode(["status" => "error", "message" => "All fields are required."]);
        exit;
    }

    // Set default values
    $purchase_date = date("Y-m-d H:i:s"); // Current timestamp
    $status = "pending"; // Default status

    // Prepare SQL statement
    $stmt = $conn->prepare("INSERT INTO tickets (user_id, movie_id, showtime_id, seat_id, purchase_date, ticket_price, status) 
                            VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("iiissds", $user_id, $movie_id, $showtime_id, $seat_id, $purchase_date, $ticket_price, $status);

    // Execute the query
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Ticket inserted successfully!"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Error inserting ticket: " . $stmt->error]);
    }

    // Close statement
    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method. Use POST."]);
}

// Close database connection
$conn->close();
?>