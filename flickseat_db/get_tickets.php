<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

error_reporting(E_ALL);
ini_set('display_errors', 1);

require_once "db_config.php";

// Check if user_id is provided
$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

if ($user_id == 0) {
    echo json_encode(["status" => "error", "message" => "User ID is required"]);
    exit;
}

$query = "SELECT t.ticket_id, t.purchase_date, t.ticket_price, t.status,
                 m.title AS movie_title,
                 s.show_day, s.show_time,
                 se.seat_name
          FROM tickets t
          JOIN movie m ON t.movie_id = m.movie_id
          JOIN showtimes s ON t.showtime_id = s.showtime_id
          JOIN seats se ON t.seat_id = se.seat_id
          WHERE t.user_id = ?";

$stmt = $conn->prepare($query);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "SQL Error: " . $conn->error]);
    exit;
}

$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$tickets = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $tickets[] = $row;
    }
    echo json_encode(["status" => "success", "tickets" => $tickets]);
} else {
    echo json_encode(["status" => "error", "message" => "No tickets found"]);
}

$stmt->close();
$conn->close();
?>
