node default{

service{"iotbroker":
   ensure => stopped,
   enable => false,
}

}