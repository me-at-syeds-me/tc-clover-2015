<?php

$data = apc_fetch('order');
$data1 = array ('order' => apc_fetch('order'),  'address' => apc_fetch('address') );
$data2 = array( 'event' => apc_fetch('event'), 'data' => $data1 );
//if (!empty (apc_fetch('order'))){
if ($data){

echo json_encode( $data2 );
}

if ($data){
apc_delete('order');
apc_delete('event');
apc_delete('address');
}

?>