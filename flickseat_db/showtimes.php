<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include 'db_config.php'; // Database connection

$movie_id = $_GET['movie_id'] ?? '';

if (empty($movie_id)) {
    echo json_encode(["status" => "error", "message" => "Movie ID is required"]);
    exit;
}

$sql = "SELECT showtime_id, show_day, show_time FROM showtimes WHERE movie_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $movie_id);
$stmt->execute();
$result = $stmt->get_result();

$showtimes = [];
while ($row = $result->fetch_assoc()) {
    $showtimes[] = $row;
}

echo json_encode(["status" => "success", "showtimes" => $showtimes]);

$stmt->close();
$conn->close();
?>
