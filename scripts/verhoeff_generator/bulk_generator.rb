require './participant_verhoeff'

ParticipantTypes = ['A', 'B', 'C']
FacilityRange = 0..5
ParticipantRange = 0..99

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

p participants
