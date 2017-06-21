#!/bin/bash
# Run in bash with the file name you want to read from at the end
# If you want to save it to a file, use >>, like: read_file.csv >> write_file.csv

grep -e '^[0-9]*00' -e '^[0-9]*50,' -e '^999' -e '^0' 
