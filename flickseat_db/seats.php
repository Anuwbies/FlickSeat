<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include 'db_config.php';

$movie_id = $_GET['movie_id'] ?? '';
$show_day = $_GET['show_day'] ?? '';
$show_time = $_GET['show_time'] ?? '';

if (empty($movie_id) || empty($show_day) || empty($show_time)) {
    echo json_encode(["status" => "error", "message" => "Movie ID, show day, and show time are required"]);
    exit;
}

// Get showtime_id for the given movie, day, and time
$sql = "SELECT showtime_id FROM showtimes WHERE movie_id = ? AND show_day = ? AND show_time = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iss", $movie_id, $show_day, $show_time);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "No showtime found"]);
    exit;
}

$showtime = $result->fetch_assoc();
$showtime_id = $showtime['showtime_id'];

// Fetch seat availability for the showtime
$sql = "SELECT seat_id, seat_name, status FROM seats WHERE showtime_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $showtime_id);
$stmt->execute();
$result = $stmt->get_result();

$seats = [];
while ($row = $result->fetch_assoc()) {
    $seats[] = $row;
}

echo json_encode(["status" => "success", "seats" => $seats]);

$stmt->close();
$conn->close();
?>
