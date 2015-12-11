node default{

service{"iotbroker":
   ensure => stopped,
   enable => false,
}


file { '/etc/init.d/iotbroker':
   ensure => 'absent',
   require => Service["iotbroker"],
 }

file{ '/root/Aeron':
   ensure => 'absent',
   require => Service["iotbroker"],
   force => 'true',
}
   

}