// ------------------------------------------------------------------
// Job Management schema

CREATE TABLE "active_job_ids" (
  bucket int,
  job_id text,
  PRIMARY KEY (bucket, job_id)
) WITH
  comment='The active set of job ids'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "active_task_ids" (
  job_id text,
  task_id text,
  PRIMARY KEY ((job_id), task_id)
) WITH
  comment='The active set of task ids as well as the job id they belong to'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "active_jobs" (
  job_id text,
  value text,
  PRIMARY KEY (job_id)
) WITH
  comment='The active jobs'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "active_tasks" (
  task_id text,
  value text,
  PRIMARY KEY (task_id)
) WITH
  comment='The active tasks'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "archived_jobs" (
  job_id text,
  value text,
  PRIMARY KEY (job_id)
) WITH
  comment='The archived jobs'
  AND compression={}
  AND default_time_to_live = 2592000; // 30 days

CREATE TABLE "archived_task_ids" (
  job_id text,
  task_id text,
  PRIMARY KEY ((job_id), task_id)
) WITH
  comment='The archived set of task ids as well as the job id they belong to'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE'
  AND default_time_to_live = 2592000; // 30 days

CREATE TABLE "archived_tasks" (
  task_id text,
  value text,
  PRIMARY KEY (task_id)
) WITH
  comment='The archived tasks'
  AND compression={}
  AND default_time_to_live = 2592000;

// ------------------------------------------------------------------
// Agent Management schema

CREATE TABLE "agent_instance_groups" (
  id text,
  value text,
  PRIMARY KEY (id)
) WITH
  comment='The agent instance groups'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "agent_instances" (
  id text,
  value text,
  PRIMARY KEY (id)
) WITH
  comment='The agent instances'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

// ------------------------------------------------------------------
// Autoscaling schema

CREATE TABLE "app_scale_policy" (
	ref_id uuid,
	job_id text,
	policy_id text,
	alarm_id text,
	status text,
	status_message text,
	value text,
	PRIMARY KEY (ref_id)
) WITH
  comment='App auto scale policies'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "app_scale_jobs" (
	job_id text,
	ref_id uuid,
	PRIMARY KEY (job_id, ref_id)
) WITH
  comment='App auto scale jobs'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

// ------------------------------------------------------------------
// Load Balancer schema

CREATE TABLE "load_balancer_jobs" (
	job_id text,
	load_balancer_id text,
	state text,
	PRIMARY KEY (job_id, load_balancer_id)
) WITH
  comment='The jobs with load balancers'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

CREATE TABLE "load_balancer_targets" (
    load_balancer_id text,
    ip_address       text,
    task_id          text,
    state            text,
    PRIMARY KEY (load_balancer_id, ip_address)
) WITH
  comment ='Load balancer targets managed by us'
  AND compression ={}
  AND compaction ={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';

// ------------------------------------------------------------------
// Scheduler schema

CREATE TABLE "system_selectors" (
  id text,
  value text,
  PRIMARY KEY (id)
) WITH
  comment='The system selectors'
  AND compression={}
  AND compaction={'min_threshold': '2', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy'}
  AND gc_grace_seconds = 21600
  AND speculative_retry = 'NONE';