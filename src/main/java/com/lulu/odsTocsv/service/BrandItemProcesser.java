package com.lulu.odsTocsv.service;

import com.lulu.odsTocsv.model.Brand;
import org.springframework.batch.item.ItemProcessor;

public class BrandItemProcesser implements ItemProcessor<Brand, Brand> {

    @Override
    public Brand process(Brand brand) throws Exception {
        return brand;
    }
}
