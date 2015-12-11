node default {

exec{"apt-get update":
  command => "/usr/bin/apt-get update",
}

package{"git":
  ensure => installed,
  before => Exec["git clone"],
  require => Exec["apt-get update"],
}


exec{"git clone":
  command => "/usr/bin/git clone https://github.com/Aeronbroker/Aeron.git",
  creates => "/root/Aeron/README.md",
  cwd => "/root/",
}

package{"default-jdk":
  ensure => installed,
  before => Exec["mvn install parent"],
  require => Exec["apt-get update"],
}

package{"maven":
  ensure => installed,
  before => Exec["mvn install parent"],
  require => Exec["apt-get update"],
}

exec{"mvn install parent":
  command => "/usr/bin/mvn install",
  cwd => "/root/Aeron/IoTbrokerParent",
  before => Exec["mvn install builder"],
  require => Exec["git clone"],
}

exec{"mvn install builder":
  command => "/usr/bin/mvn install",
  cwd => "/root/Aeron/eu.neclab.iotplatform.iotbroker.builder",
  timeout => 0,
  creates => "/root/Aeron/eu.neclab.iotplatform.iotbroker.builder/target/",
}

file{"/root/Aeron/IoTBroker-runner/unix64_start-IoTBroker_as_Demon.sh":
  mode => "+x",
  require => Exec["git clone"],
}

file{"/root/Aeron/IoTBroker-runner/unix64_start-IoTBroker.sh":
  mode => "+x",
  require => Exec["git clone"],
}

file{"/root/Aeron/IoTBroker-runner/unix64_stop-IoTBroker.sh":
  mode => "+x",
  require => Exec["git clone"],
}

exec{"/root/Aeron/IoTBroker-runner/unix64_start-IoTBroker_as_Demon.sh":
    cwd => "/root/Aeron/IoTBroker-runner/",
  require => [Exec["mvn install builder"],File["/root/Aeron/IoTBroker-runner/unix64_start-IoTBroker_as_Demon.sh"]],

}

file{'/etc/init.d':
  ensure => "directory",
  mode => "0755"
}

 file { '/etc/init.d/iotbroker':
   ensure => 'present',
   owner  => 'root',
   group  => 'root',
   mode   => '0755',
   before => Service['iotbroker'],
   source => "/root/Aeron/IoTBroker-runner/unix64_service"
 }

service{"iotbroker":
   ensure => running,
   enable => true,
   require => File['/etc/init.d/iotbroker'],

}


}
