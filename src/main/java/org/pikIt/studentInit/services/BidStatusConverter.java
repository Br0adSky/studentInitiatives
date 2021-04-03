package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.BidStatus;

import javax.persistence.AttributeConverter;

public class BidStatusConverter implements AttributeConverter<BidStatus, String> {

    @Override
    public String convertToDatabaseColumn(BidStatus attribute) {
        return attribute.toString();
    }

    @Override
    public BidStatus convertToEntityAttribute(String dbData) {
        return BidStatus.getByName(dbData.toUpperCase());
    }
}
