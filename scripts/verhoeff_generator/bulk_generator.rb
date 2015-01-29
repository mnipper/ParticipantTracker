require './participant_verhoeff'
require 'csv'

ParticipantTypes = ['N', 'E', 'V']
FacilityRange = 1..599
ParticipantRange = 1..99
CSVFilePath = "participant_ids.csv"

participants = []

ParticipantTypes.each do |type|
  FacilityRange.each do |facility|
    ParticipantRange.each do |participant|
      padded_facility = facility.to_s.rjust(3,'0')
      padded_participant = participant.to_s.rjust(2,'0')
      pid = "#{type}-#{padded_facility}-#{padded_participant}" 
      pid_checked = ParticipantVerhoeff.generate_check(pid)
      participants << [type, padded_facility, padded_participant, pid_checked[-1,1], pid_checked] 
    end
  end
end

CSV.open(CSVFilePath, "wb") do |csv|
  csv << ['Participant Type', 'Center', 'ID', 'Check', 'Participant ID']
  participants.each do |participant|
    csv << participant
  end
end
