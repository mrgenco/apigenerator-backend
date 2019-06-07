package com.mrg.apigenerator;

import java.util.List;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

public class ApiGenReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

	public ApiGenReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	public String columnToPropertyName(TableIdentifier table, String column) {
		if (column.endsWith("PK")) {
			return "id";
		} else {
			return super.columnToPropertyName(table, column);
		}
	}

	public boolean excludeForeignKeyAsCollection(String FK, TableIdentifier tableIdentifier, List list,
			TableIdentifier tableIdentifier1, List list1) {
		boolean collectionInverse = isForeignKeyCollectionInverse(FK, tableIdentifier, list, tableIdentifier1, list1);
		if (collectionInverse) {
			return true;
		}
		return false;
	}
}
