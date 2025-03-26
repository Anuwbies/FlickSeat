<?php
require 'db_config.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");
header("Content-Type: application/json");

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $user_id = $_POST['user_id'];
    $food_id = isset($_POST['food_id']) ? $_POST['food_id'] : NULL;
    $drink_id = isset($_POST['drink_id']) ? $_POST['drink_id'] : NULL;
    $quantity = $_POST['quantity'];

    if ($quantity <= 0) {
        $response["status"] = "error";
        $response["message"] = "Quantity must be greater than 0.";
        echo json_encode($response);
        exit;
    }

    $sql = "INSERT INTO orders (user_id, food_id, drink_id, quantity) VALUES (?, ?, ?, ?)";
    
    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("iisi", $user_id, $food_id, $drink_id, $quantity);

        if ($stmt->execute()) {
            $response["status"] = "success";
            $response["message"] = "Order placed successfully.";
        } else {
            $response["status"] = "error";
            $response["message"] = "Database error: " . $stmt->error;
        }

        $stmt->close();
    } else {
        $response["status"] = "error";
        $response["message"] = "Query preparation failed: " . $conn->error;
    }

    $conn->close();
} else {
    $response["status"] = "error";
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
