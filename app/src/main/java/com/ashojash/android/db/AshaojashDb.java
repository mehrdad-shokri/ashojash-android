package com.ashojash.android.db;

import java.util.List;

public abstract class AshaojashDb<T> {
    public abstract int updateRecords(List<T> items);
}
