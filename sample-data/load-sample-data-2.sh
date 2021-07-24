#!/bin/bash

mongoimport --db memesDB --collection memes --drop --jsonArray --file ./sample-data-2.json
