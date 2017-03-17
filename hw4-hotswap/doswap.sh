#/bin/bash

function doswap {
    docker ps -a  > /tmp/yy_xx$$
    if grep --quiet web1 /tmp/yy_xx$$
     then
     	docker run -dP --net ecs189_default --name web2 hw4
		sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
        docker rm -f `docker ps -a | grep $1  | sed -e 's: .*$::'`

	 else
	 	docker run -dP --net ecs189_default --name web1 activity
		sleep 10 && docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
        docker rm -f `docker ps -a | grep hw4  | sed -e 's: .*$::'`

   fi
}

doswap $1







