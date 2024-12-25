package kz.offerprocessservice.scheduler;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PointOfSaleService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class PriceListScheduler {

    @Value("${minio.prefix-to-delete}")
    private String minioPrefixToDelete;

    private final MinioService minioService;
    private final PriceListService priceListService;
    private final PointOfSaleService pointOfSaleService;
    private final ExecutorService validationExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService processingExecutor = Executors.newFixedThreadPool(2);
    private final BlockingQueue<PriceListEntity> validationQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<PriceListEntity> processingQueue = new LinkedBlockingQueue<>();

    @Scheduled(fixedRate = 60000)
    public void scheduleValidation() {
        Set<PriceListEntity> priceLists = priceListService.findNewPriceLists();

        if (!priceLists.isEmpty()) {
            for (PriceListEntity priceList : priceLists) {
                priceList.setStatus(PriceListStatus.VALIDATION_WAITING);
                priceListService.updateStatus(priceList);
                validationQueue.offer(priceList);
            }

            processValidationQueue();
        }
    }

    private void processValidationQueue() {
        for (int i = 0; i < 2; i++) {
            validationExecutor.submit(() -> {
                while (true) {
                    try {
                        PriceListEntity priceList = validationQueue.take();
                        priceList.setStatus(PriceListStatus.ON_VALIDATION);
                        priceListService.updateStatus(priceList);
                        boolean validated = scheduleValidation(priceList);
                        if (validated) {
                            processingQueue.offer(priceList);
                        }
                    } catch (InterruptedException | CustomException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    private boolean scheduleValidation(PriceListEntity priceList) throws CustomException {
        Set<String> pos = pointOfSaleService.getAllPosNames(priceList.getMerchantId());
        InputStream is = minioService.getFile(priceList.getUrl().replaceFirst(minioPrefixToDelete, ""));
        String failReason = null;
        PriceListStatus status;

        try {
            if (FileUtils.validatePriceList(is, pos)) {
                status = PriceListStatus.VALIDATED;
            } else {
                failReason = "Incorrect headers.";
                status = PriceListStatus.VALIDATION_FAILED;
            }
        } catch (Exception e) {
            failReason = "Unable to validate file: " + e.getMessage();
            status = PriceListStatus.VALIDATION_FAILED;
        }

        priceList.setStatus(status);
        priceList.setFailReason(failReason);
        priceListService.updateStatus(priceList);

        return status == PriceListStatus.VALIDATED;
    }
    //
    //    @Scheduled(fixedRate = 60000)
    //    public void scheduleProcessing() {
    //        if (!validationQueue.isEmpty()) {
    //            processProcessingQueue();
    //        }
    //    }
    //
    //    private void processProcessingQueue() {
    //        for (int i = 0; i < 2; i++) {
    //            processingExecutor.submit(() -> {
    //                while (true) {
    //                    try {
    //                        PriceListEntity ple = processingQueue.take();
    //                        ple.setStatus(PriceListStatus.PROCESSING);
    //                        priceListService.updateStatus(ple);
    //                        processFile(ple);
    //                    } catch (InterruptedException | CustomException e) {
    //                        Thread.currentThread().interrupt();
    //                        break;
    //                    }
    //                }
    //            });
    //        }
    //    }
    //
    //    private void processFile(PriceListEntity ple) throws CustomException {
    //        InputStream is = minioService.getFile(ple.getUrl().replaceFirst(minioPrefixToDelete, ""));
    //
    //        try {
    //
    //        }
    //    }
}
