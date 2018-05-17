<?php
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;


$app = new \Slim\App;
$app->get('/api/customers', function(Request $request, Response $response){
    $sql = "SELECT * FROM CUSTOMERS";

    try{
      $db = new db();
      $db = $db->connect();

      $stmt = $db->query($sql);
      $customers = $stmt->fetchAll(PDO::FETCH_OBJ);
      $db = null;
      echo json_encode($customers);
    }
    catch(PDOException $e){
      echo '{"error": {"text": '.$e->getMessage().'}';
    }
});


//get a single customer with name
$app->get('/api/customer/{name}', function(Request $request, Response $response){
    $name = $request->getAttribute('name');
    $sql = "SELECT * FROM CUSTOMERS WHERE PERSON = '$name'";

    try{
      $db = new db();
      $db = $db->connect();

      $stmt = $db->query($sql);
      $customer = $stmt->fetchAll(PDO::FETCH_OBJ);
      $db = null;
      echo json_encode($customer);
    }
    catch(PDOException $e){
      echo '{"error": {"text": '.$e->getMessage().'}';
    }
});

$app->post('/api/customer/select', function(Request $request, Response $response){
    $person = $request->getParam('PERSON');
    $organization = $request->getParam('ORGANIZATION');
    $sql = "SELECT * FROM CUSTOMERS WHERE PERSON = '$person' AND ORGANIZATION = '$organization'";

    try{
      $db = new db();
      $db = $db->connect();

      $stmt = $db->query($sql);
      $customer = $stmt->fetchAll(PDO::FETCH_OBJ);
      $db = null;
      echo json_encode($customer);
    }
    catch(PDOException $e){
      echo '{"error": {"text": '.$e->getMessage().'}';
    }
});




