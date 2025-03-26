<?php
require 'db_config.php'; // Database connection file

// Get user_id from request
if (!isset($_GET['user_id'])) {
    echo json_encode(["status" => "error", "message" => "User ID required"]);
    exit();
}

$user_id = intval($_GET['user_id']); // Convert to integer

// SQL Query to get all orders with food_name and drink_name
$sql = "
    SELECT 
        o.order_id,
        o.user_id,
        o.food_id,
        f.food_name AS food_name,  -- Corrected here
        o.drink_id,
        d.drink_name AS drink_name, -- Make sure drink_name exists in drinks table
        o.quantity,
        o.status
    FROM orders o
    LEFT JOIN foods f ON o.food_id = f.food_id
    LEFT JOIN drinks d ON o.drink_id = d.drink_id
    WHERE o.user_id = ?
    ORDER BY o.order_id DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $orders[] = $row;
    }
    echo json_encode(["status" => "success", "orders" => $orders]);
} else {
    echo json_encode(["status" => "error", "message" => "No orders found"]);
}

$stmt->close();
$conn->close();
?>
