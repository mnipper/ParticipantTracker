require './verhoeff'

module ParticipantVerhoeff
  def self.generate_check(id)
    raise 'Improper format' if id !~ /^[A-Z]\-\d{3}\-\d{2}$/
    cleaned = id.gsub("\-", '')
    cleaned = "#{cleaned[0].ord}#{cleaned[1..-1]}"
    checksum_digit = Verhoeff.checksum_digit_of cleaned.to_i 
    "#{id}-#{(65 + checksum_digit).chr}"
  end
end
