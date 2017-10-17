/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.netflix.titus.master.store;

import java.io.IOException;
import java.util.List;

import io.netflix.titus.api.store.v2.InvalidJobException;
import io.netflix.titus.api.store.v2.V2JobMetadata;
import rx.Observable;

public interface V2StorageProvider {

    /**
     * Store to persistence newly created job with given metadata object. This is expected to fail if job with the same
     * jobId as given in the <code>jobMetadata</code> object already exists in persistence store.
     *
     * @param jobMetadata
     * @throws JobAlreadyExistsException If a job with same id as in the given metadata object already exists
     * @throws java.io.IOException
     */
    void storeNewJob(V2JobMetadataWritable jobMetadata)
            throws JobAlreadyExistsException, IOException;

    void updateJob(V2JobMetadataWritable jobMetadata)
            throws InvalidJobException, IOException;

    // TODO: carve out archived jobs related functions into separate storage provider

    /**
     * Mark the job as not active and move it to an inactive archived collection of jobs.
     *
     * @param jobId The Job Id of the job to archive
     * @throws IOException upon errors with storage invocation
     */
    void archiveJob(String jobId) throws IOException;

    /**
     * Delete the job metadata permanently.
     *
     * @param jobId The Job Id of the job to delete
     * @throws InvalidJobException If there is no such job to delete
     * @throws IOException         Upon errors with storage invocation
     */
    void deleteJob(String jobId) throws InvalidJobException, IOException;

    void storeStage(V2StageMetadataWritable msmd)
            throws IOException;

    void updateStage(V2StageMetadataWritable msmd) throws IOException;

    /**
     * Store a new worker for the given job and stage number. This will be called only once for a given
     * worker. However, it is possible that concurrent calls can be made on a <code>jobId</code>, each with a
     * different worker.
     *
     * @param workerMetadata The worker metadata to store.
     * @throws IOException
     */
    void storeWorker(V2WorkerMetadataWritable workerMetadata)
            throws IOException;

    /**
     * Store multiple new workers for the give job. This is called only once for a given worker. This method enables
     * optimization by calling storage once for multiple workers.
     *
     * @param jobId   The Job ID.
     * @param workers The list of workers to store.
     * @throws IOException if there were errors storing the workers.
     */
    void storeWorkers(String jobId, List<V2WorkerMetadataWritable> workers)
            throws IOException;

    /**
     * Store a new worker and update existing worker of a job atomically. Either both are stored or none is.
     *
     * @param worker1 Existing worker to update.
     * @param worker2 New worker to store.
     * @throws IOException
     * @throws InvalidJobException If workers don't have the same JobId.
     */
    void storeAndUpdateWorkers(V2WorkerMetadataWritable worker1, V2WorkerMetadataWritable worker2)
            throws InvalidJobException, IOException;

    /**
     * Update (overwrite) existing worker metadata with the given metadata.
     *
     * @param mwmd Worker metadata to update
     * @throws IOException
     */
    void updateWorker(V2WorkerMetadataWritable mwmd)
            throws IOException;

    /**
     * Initialize and return all existing jobs from persistence, including all corresponding job stages and workers.
     *
     * @return List of job metadata objects
     * @throws IOException
     */
    List<V2JobMetadataWritable> initJobs()
            throws IOException;

    Observable<V2JobMetadata> initArchivedJobs();

    /**
     * Initialize and return all existing NamedJobs from persistence.
     *
     * @return List of {@link NamedJob} objects.
     * @throws IOException Upon error connecting to or reading from persistence.
     */
    List<NamedJob> initNamedJobs() throws IOException;

    /**
     * Initialize and return completed jobs of all NamedJobs in the system.
     *
     * @return An Observable of all completed jobs for all NamedJobs.
     * @throws IOException Upon error connecting to or reading from persistence.
     */
    Observable<NamedJob.CompletedJob> initNamedJobCompletedJobs() throws IOException;

    /**
     * Archives worker. This is usually called when a worker enters error state. It is expected that archived workers
     * are moved out from regular store elsewhere so when jobs are loaded they do not contain archived workers.
     *
     * @param mwmd Worker metadata to archive
     * @throws IOException
     */
    void archiveWorker(V2WorkerMetadataWritable mwmd)
            throws IOException;

    List<V2WorkerMetadataWritable> getArchivedWorkers(String jobid)
            throws IOException;

    void storeNewNamedJob(NamedJob namedJob) throws JobNameAlreadyExistsException, IOException;

    void updateNamedJob(NamedJob namedJob) throws InvalidNamedJobException, IOException;

    boolean deleteNamedJob(String name) throws IOException;

    void storeCompletedJobForNamedJob(String name, NamedJob.CompletedJob job) throws IOException;

    void removeCompledtedJobForNamedJob(String name, String jobId) throws IOException;

    V2JobMetadataWritable loadArchivedJob(String jobId) throws IOException;

    void shutdown();
}
