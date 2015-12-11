node default{

service{"iotbroker":
   ensure => running,
   enable => true,
}

}