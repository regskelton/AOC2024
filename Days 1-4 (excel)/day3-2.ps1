$egg = Get-Content day3-input.txt | select-string -Pattern "(do\(\)|don't\(\)|mul\(\d+,\d+\))" -AllMatches 
$egg.Matches.Value > .\day3-2.output
