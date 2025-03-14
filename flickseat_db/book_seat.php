<?php
include 'db_config.php'; // Include database connection

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $seat_id = $_POST['seat_id'] ?? null;

    if (!$seat_id) {
        echo json_encode(["status" => "error", "message" => "Seat ID is required."]);
        exit;
    }

    // Check if the seat exists
    $checkQuery = "SELECT * FROM seats WHERE seat_id = ?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("i", $seat_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "Seat not found."]);
        exit;
    }

    // Update seat status to 'taken'
    $updateQuery = "UPDATE seats SET status = 'taken' WHERE seat_id = ?";
    $stmt = $conn->prepare($updateQuery);
    $stmt->bind_param("i", $seat_id);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Seat booked successfully."]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to book seat."]);
    }

    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method."]);
}

$conn->close();
?>
