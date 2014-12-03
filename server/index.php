<?php
require 'flight/Flight.php';

Flight::register('db', 'PDO', array('mysql:host=localhost;dbname=techobyt_mobipair','techobyt_mp','M0b!P@!r'));

class Devices {
    public static function getDevices(&$db, $id) {
        if(!empty($id)) {
            $sql = "SELECT * FROM `devices` WHERE dev_id='".$id."' ORDER BY dev_date DESC";
        } else {
            $sql = "SELECT * FROM `devices` ORDER BY dev_date DESC";
        }
        
        $res = $db->query($sql);
        if($res != false) {
            $arrDevices = $res->fetchAll(PDO::FETCH_ASSOC);
            return $arrDevices;
        }
        return $res;
    }
    
    public static function generateToken(&$db) {
        $db = Flight::db(false);
        $sqlToken = 'SELECT FLOOR(1000 + RAND() * 998999) AS token
                       FROM `devices`
                       WHERE "dev_token" NOT IN (SELECT dev_token FROM `devices` WHERE dev_token IS NOT NULL) LIMIT 1';
        $res = $db->query($sqlToken);
        $row = $res->fetch(PDO::FETCH_ASSOC);
        return $row['token'];
    }
    
    public static function addDevice(&$db, $id, $gcmId, $title, $email) {
        $res = self::getDevices($db, $id);
        if($res == false) {
            $token = self::generateToken($db);
            $sql = "INSERT INTO `devices` (dev_id,dev_date,dev_token,dev_title,dev_email) VALUES (:id,NOW(),:token,:title,:email)";
            $q = $db->prepare($sql);
            $q->execute(array(':id'=>$id,':token'=>$token, ':title'=>$title, ':email'=>$email));
            $dbId = $db->lastInsertId();
            if($dbId) {
                $sql = "INSERT INTO `notify_gcm` (dev_id,reg_id) VALUES (:id,:gcmId)";
                $q = $db->prepare($sql);
                $q->execute(array(':id'=>$dbId,':gcmId'=>$gcmId));
            }
            $res = self::getDevices($db, $id);
        }
        return $res;
    }
    
    public static function updateDevice(&$db, $id, $title) {
        $sql = "UPDATE `devices` SET dev_title=? WHERE id=?";
        $q = $db->prepare($sql);
        $q->execute(array($title,$id));
    }
}

class PairedDevices {
    public static function getPairedDeviceInfo(&$db, $forId) {
        $sql = "SELECT dv.dev_title,dp.pair_date,dp.status
                  FROM `devices_pair` AS dp
                  INNER JOIN `devices` AS dv ON dv.id=dp.dev_id_2
                  WHERE dp.dev_id_1 IN (SELECT dv1.id FROM `devices` AS dv1 WHERE dev_id='$forId')";
        $res = $db->query($sql);
        $dev1All = $res->fetchAll(PDO::FETCH_ASSOC);
        
        $sql = "SELECT dv.dev_title,dp.pair_date,dp.status
                  FROM `devices_pair` AS dp
                  INNER JOIN `devices` AS dv ON dv.id=dp.dev_id_1
                  WHERE dp.dev_id_2 IN (SELECT dv1.id FROM `devices` AS dv1 WHERE dev_id='$forId')";
        $res = $db->query($sql);
        $dev2All = $res->fetchAll(PDO::FETCH_ASSOC);
        
        $mix = array_merge($dev1All, $dev2All);
        return $mix;
    }
    
    public static function pair2DevicesById(&$db, $id1, $id2) {
        $res = "";
        $sql = "SELECT *
                  FROM `devices_pair`
                  WHERE dev_id_1 IN (SELECT id FROM `devices` WHERE dev_id='$id1')
                    AND dev_id_2 IN (SELECT id FROM `devices` WHERE dev_id='$id2')";
        $res = $db->query($sql);
        $pair = $res->fetch(PDO::FETCH_ASSOC);
        if($pair==false) {
            $sql = "INSERT INTO `devices_pair` (dev_id_1,dev_id_2,pair_date)
                    VALUES ((SELECT id FROM `devices` WHERE dev_id=':id1'),(SELECT id FROM `devices` WHERE dev_id=':id2'),NOW())";
            $q = $db->prepare($sql);
            
            if($q == false) { 
                throw new Exception(print_r($db->errorInfo(),1).PHP_EOL.$sql);
            }
            $res = $q->execute(array(':id1'=>$id1,':id2'=>$id2));
            if($res == false) {
                throw new Exception(print_r($db->errorInfo(),1).PHP_EOL.$sql);
            }
        } else {
            $res = "Already paired";
        }
        return $res;
    }
    
    public static function pair2DevicesByToken(&$db, $token1, $token2) {
        $res = "";
        $sql = "SELECT *
                  FROM `devices_pair`
                  WHERE dev_id_1 IN (SELECT id FROM `devices` WHERE dev_token='$token1')
                    AND dev_id_2 IN (SELECT id FROM `devices` WHERE dev_token='$token2')";
        $res = $db->query($sql);
        $pair = $res->fetch(PDO::FETCH_ASSOC);
        if($pair==false) {
            $sql = "INSERT INTO `devices_pair` (dev_id_1,dev_id_2,pair_date)
                    VALUES ((SELECT id FROM `devices` WHERE dev_token=':token1'),(SELECT id FROM `devices` WHERE dev_token=':token2'),NOW())";
            $q = $db->prepare($sql);
            
            if($q == false) { 
                throw new Exception(print_r($db->errorInfo(),1).PHP_EOL.$sql);
            }
            $res = $q->execute(array(':token1'=>$token1,':token2'=>$token2));
            if($res == false) {
                throw new Exception(print_r($db->errorInfo(),1).PHP_EOL.$sql);
            }
        } else {
            $res = "Already paired";
        }
        return $res;
    }

    public static function getPairedDevices(&$db, $forId) {
        return array( "APA91bGH6xZATCWX1whCy234x-VFzDkww7jcQv3tpa2w_4RfAqh6HPo3V1Tz9GQDCJhqkiK681nAGupKKael1Efz-cRlnFjnaB4kYWK63O_i9KCuqlzcmg17ZbUICncV3QUK1KBNhC4dvyC7cZJgc7UGNPvnulotaw",
                      "APA91bGfZZix_Ms_2uDG-vlHuG8K3yA6jUUqKtLks3mHEqU2TEIwL7TjSnjV5jUqbFaCeYx_tcpabz1CqSIxC0c3P1YleA5vvUMoZt9dZ7QFs-IYF4qIglyRReIo0plEiwyMpzQj0OlSNVAU6qBzw56yHViJeYRlAg");
    }
}

class Notify {
    public static function google_push_notification ($arrGCM_ids, $message) {
        $apiKey = "AIzaSyCIeG8C6e1w8u2uqZJaRJJQCvkrMK_PVvI";
        $url = 'https://android.googleapis.com/gcm/send';

        $fields = array(
            'registration_ids' => $arrGCM_ids,
            'data' => array( "message" => $message ),
        );
        $headers = array(
            'Authorization: key=' . $apiKey,
            'Content-Type: application/json'
        );

        // Open connection
        $ch = curl_init();

        // Set the URL, number of POST vars, POST data
        curl_setopt( $ch, CURLOPT_URL, $url);
        curl_setopt( $ch, CURLOPT_POST, true);
        curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true);
        //curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields));

        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        // curl_setopt($ch, CURLOPT_POST, true);
        // curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode( $fields));

        // Execute post
        $result = curl_exec($ch);

        // Close connection
        curl_close($ch);
        return $result;
    }
}

/********************************************************/

Flight::route('/', function(){
    echo 'hello world!';
});

/********************************************************/

Flight::route('GET /devices', function(){
    $db = Flight::db(false);
    $id = Flight::request()->query['id'];
    if(empty($id)) {
        $str = Flight::request()->getBody();
        parse_str($str, $output);
        $id = $output['id'];
    }
    $arr = Devices::getDevices($db, $id);
    print(json_encode($arr));
});

Flight::route('POST /devices', function(){
    $db = Flight::db(false);
    $id = Flight::request()->query['id'];
    $gcmId = Flight::request()->query['gcmId'];
    $title = Flight::request()->query['title'];
    $email = Flight::request()->query['email'];
    if(empty($id)) {
        $str = Flight::request()->getBody();
        parse_str($str, $output);
        $id = $output['id'];
        $gcmId = $output['gcmId'];
        $title = $output['title'];
        $email = $output['email'];
    }
    if(empty($id)) {
        echo "Error: no Id";
        return;
    }
    if(empty($gcmId)) {
        echo "Error: provide GCM Id";
        return;
    }
    $arr = Devices::addDevice($db, $id, $gcmId, $title, $email);
    print(json_encode($arr));
});

Flight::route('PUT /devices', function(){
    $db = Flight::db(false);
    $id = Flight::request()->query['id'];
    $title = Flight::request()->query['title'];
    if(empty($id)) {
        $str = Flight::request()->getBody();
        parse_str($str, $output);
        $id = $output['id'];
        $id = $output['title'];
    }
    if(empty($id)) {
        echo "Error: no Id";
        return;
    }
    if(empty($title)) {
        echo "Error: no Title";
        return;
    }
    Devices::updateDevice($db, $id, $title);
});

/********************************************************/

Flight::route('GET /pairs', function(){
    $db = Flight::db(false);
    $id = Flight::request()->query['id'];
    if(empty($id)) {
        $str = Flight::request()->getBody();
        parse_str($str, $output);
        $id = $output['id'];
    }
    $arrAll = PairedDevices::getPairedDeviceInfo($db, $id);
    echo json_encode($arrAll);
});

Flight::route('POST /pairs', function(){
    $db = Flight::db(false);
    $str = Flight::request()->getBody();
    parse_str($str, $bodyParams);
    $id1 = Flight::request()->query['id1'];
    $id2 = Flight::request()->query['id2'];
    $res = "";
    if(empty($id1)) {
        $id1 = $bodyParams['id1'];
        $id2 = $bodyParams['id2'];
    }
    if(!empty($id1)) {
        $res = PairedDevices::pair2DevicesById($db, $id1, $id2);
    } else {
        $token1 = Flight::request()->query['token1'];
        $token2 = Flight::request()->query['token2'];
        if(empty($token1)) {
            $token1 = $bodyParams['token1'];
            $token2 = $bodyParams['token2'];
        }
        
        if(!empty($token1)) {
            $res = PairedDevices::pair2DevicesByToken($db, $token1, $token2);
        } else {
            $res = "Error: pass either id or token";
        }
    }
    
    echo $res;
});

/********************************************************/

Flight::route('GET /notify', function(){
    
    echo "List of devices to be notified";
});

Flight::route('POST /notify', function(){
    $id = Flight::request()->query['id'];
    $msg = Flight::request()->query['msg'];
    $paramType = "querystring";
    if(empty($msg)) {
        $str = Flight::request()->getBody();
        parse_str($str, $output);
        $id = $output['id'];
        $msg = $output['msg'];
        $paramType = "content";
    }
    
    $arrGCM_ids = PairedDevices::getPairedDevices($db, $id);
    
    echo Notify::google_push_notification($arrGCM_ids, $msg);
});

/********************************************************/

Flight::start();
?>