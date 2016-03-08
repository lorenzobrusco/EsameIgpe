package logic.info;

public class WhatIs<B, O> {

    public B first;

    public O second;

    public WhatIs(B first, O second) {
	set(first, second);
    }

    public void set(B first, O second) {
	this.first = first;
	this.second = second;
    }

}
