<?php
include 'db_config.php';

$response = array();

$sql_foods = "SELECT food_id, food_name, food_price FROM foods";
$sql_drinks = "SELECT drink_id, drink_name, drink_price FROM drinks";

$foods_result = mysqli_query($conn, $sql_foods);
$drinks_result = mysqli_query($conn, $sql_drinks);

if ($foods_result && $drinks_result) {
    $foods = array();
    $drinks = array();

    while ($row = mysqli_fetch_assoc($foods_result)) {
        $foods[] = array(
            "id" => (int)$row["food_id"],
            "name" => $row["food_name"],
            "price" => (int)$row["food_price"]
        );
    }

    while ($row = mysqli_fetch_assoc($drinks_result)) {
        $drinks[] = array(
            "id" => (int)$row["drink_id"],
            "name" => $row["drink_name"],
            "price" => (int)$row["drink_price"]
        );
    }

    $response["status"] = "success";
    $response["foods"] = $foods;
    $response["drinks"] = $drinks;
} else {
    $response["status"] = "error";
    $response["message"] = "Failed to retrieve data";
}

echo json_encode($response);
mysqli_close($conn);
?>
