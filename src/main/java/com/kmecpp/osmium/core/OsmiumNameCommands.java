package com.kmecpp.osmium.core;

public class OsmiumNameCommands {

	/*
	 * select count(DISTINCT user_id) from osmium_names;
	 * select user_id, count(*) from osmium_names group by user_id;
	 * select user_id, count(*) as name_count from osmium_names group by user_id having count(*) > 1 limit 10;
	 * 
	 * TODO:
	 * - Get total users and total users who have changed names
	 * 
	 * - Get most recent name changes on the server
	 *   - select user_id, count(*) as name_count from osmium_names group by user_id having count(*) > 1 order by first_seen desc limit 10;
	 * 
	 * - Get full name history for a certain player
	 * - Get all UUIDs which used a certain name
	 */
}
