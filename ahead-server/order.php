<?php
// Set global Session

apc_add("order", $_REQUEST['order']);
apc_add("event", $_REQUEST['event']);
apc_add("address", $_REQUEST['address']);

echo "order id stored: " . $_REQUEST['order'];
// may be setup heading out a little later?

?>