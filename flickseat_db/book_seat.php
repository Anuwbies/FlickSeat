<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
include 'db_config.php';

$seat_id = $_POST['seat_id'] ?? '';

if (empty($seat_id)) {
    echo json_encode(["status" => "error", "message" => "Seat ID is required"]);
    exit;
}

// Update seat status to "taken"
$sql = "UPDATE seats SET status = 'taken' WHERE seat_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $seat_id);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    echo json_encode(["status" => "success", "message" => "Seat booked successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed to book seat"]);
}

$stmt->close();
$conn->close();
?>
