/*
 * Copyright (C) 2022 Vaticle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vaticle.typedb.core.migrator;

import com.vaticle.typedb.core.TypeDB;
import com.vaticle.typedb.core.migrator.database.DatabaseExporter;
import com.vaticle.typedb.core.migrator.database.DatabaseImporter;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class MigratorService extends MigratorGrpc.MigratorImplBase {
    private static final Logger LOG = LoggerFactory.getLogger(MigratorService.class);

    private final TypeDB.DatabaseManager databaseMgr;
    private final String version;

    public MigratorService(TypeDB.DatabaseManager databaseMgr, String version) {
        this.databaseMgr = databaseMgr;
        this.version = version;
    }

    @Override
    public void exportDatabase(MigratorProto.Export.Req request, StreamObserver<MigratorProto.Export.Progress> responseObserver) {
        try {
            DatabaseExporter exporter = new DatabaseExporter(
                    databaseMgr, request.getDatabase(), Paths.get(request.getSchemaFile()),
                    Paths.get(request.getDataFile()), version
            );
            CompletableFuture<Void> migratorJob = CompletableFuture.runAsync(exporter::run);
            while (!migratorJob.isDone()) {
                Thread.sleep(1000);
                responseObserver.onNext(exporter.getProgress());
            }
            migratorJob.join();
            responseObserver.onCompleted();
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            responseObserver.onError(exception(e));
        }
    }

    @Override
    public void importDatabase(MigratorProto.Import.Req request, StreamObserver<MigratorProto.Import.Progress> responseObserver) {
        DatabaseImporter importer = null;
        try {
            importer = new DatabaseImporter(
                    databaseMgr, request.getDatabase(), Paths.get(request.getSchemaFile()),
                    Paths.get(request.getDataFile()), version
            );
            CompletableFuture<Void> migratorJob = CompletableFuture.runAsync(importer::run);
            while (!migratorJob.isDone()) {
                Thread.sleep(1000);
                responseObserver.onNext(importer.getProgress());
            }
            migratorJob.join();
            responseObserver.onCompleted();
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            responseObserver.onError(exception(e));
        } finally {
            if (importer != null) importer.close();
        }
    }

    public static StatusRuntimeException exception(Throwable e) {
        if (e instanceof StatusRuntimeException) {
            return (StatusRuntimeException) e;
        } else {
            return Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException();
        }
    }
}
