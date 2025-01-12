$egg = Get-Content day3-input-dos.txt | select-string -Pattern "mul\(\d+[,]{1}\d+\)" -AllMatches
$egg.Matches.Value > day3-output-dos