<?php

$order = apc_fetch('order');
echo  ("order" . $order );
echo  ( apc_fetch('event')); 

//apc_fetch('order');
apc_delete('order'); 


?>