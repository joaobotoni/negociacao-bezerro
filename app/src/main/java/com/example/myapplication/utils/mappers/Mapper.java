package com.example.myapplication.utils.mappers;

public interface Mapper<I, O> {
    O mapTo(I i);
    I mapFrom(O o);
}
