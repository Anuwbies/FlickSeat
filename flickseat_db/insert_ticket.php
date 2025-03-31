<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");

require_once "db_config.php"; // Include database configuration

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $user_id = $_POST['user_id'] ?? null;
    $movie_id = $_POST['movie_id'] ?? null;
    $showtime_id = $_POST['showtime_id'] ?? null;
    $seat_id = $_POST['seat_id'] ?? null;
    $ticket_price = $_POST['ticket_price'] ?? null;

    if (!$user_id || !$movie_id || !$showtime_id || !$seat_id || !$ticket_price) {
        echo json_encode(["status" => "error", "message" => "All fields are required."]);
        exit;
    }

    $purchase_date = date("Y-m-d H:i:s");
    $status = "pending";

    // **Find the first available ticket_id (gap filling logic)**
    $missingIdQuery = "SELECT MIN(t1.ticket_id + 1) AS available_id 
                       FROM tickets t1 
                       LEFT JOIN tickets t2 ON t1.ticket_id + 1 = t2.ticket_id 
                       WHERE t2.ticket_id IS NULL 
                       AND t1.ticket_id + 1 > 1";

    $missingIdResult = $conn->query($missingIdQuery);
    $availableTicketId = null;

    if ($missingIdResult && $missingIdResult->num_rows > 0) {
        $row = $missingIdResult->fetch_assoc();
        $availableTicketId = $row['available_id'];
    }

    // Check if ticket_id 1 is available
    $checkId1Query = "SELECT ticket_id FROM tickets WHERE ticket_id = 1";
    $checkId1Result = $conn->query($checkId1Query);
    if ($checkId1Result->num_rows == 0) {
        $availableTicketId = 1;
    }

    // If no gaps were found, assign the next highest ticket_id
    if ($availableTicketId === null) {
        $maxIdQuery = "SELECT IFNULL(MAX(ticket_id), 0) + 1 AS next_id FROM tickets";
        $maxIdResult = $conn->query($maxIdQuery);
        $maxIdRow = $maxIdResult->fetch_assoc();
        $availableTicketId = $maxIdRow['next_id'];
    }

    // Insert new ticket with available ticket_id
    $stmt = $conn->prepare("INSERT INTO tickets (ticket_id, user_id, movie_id, showtime_id, seat_id, purchase_date, ticket_price, status) 
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("iiiissds", $availableTicketId, $user_id, $movie_id, $showtime_id, $seat_id, $purchase_date, $ticket_price, $status);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Ticket inserted successfully!", "ticket_id" => $availableTicketId]);
    } else {
        echo json_encode(["status" => "error", "message" => "Error inserting ticket: " . $stmt->error]);
    }

    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method. Use POST."]);
}

$conn->close();
?>
