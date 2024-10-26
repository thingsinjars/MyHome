/*
 * Copyright 2020 Prathab Murugan
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

package com.myhome.services.springdatajpa;

import com.myhome.domain.HouseMember;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.HouseMemberDocumentService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles CRUD operations for house member documents, including uploading, updating,
 * and deleting documents. It also compresses images to meet file size limits.
 */
@Service
public class HouseMemberDocumentSDJpaService implements HouseMemberDocumentService {

  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  @Value("${files.compressionBorderSizeKBytes}")
  private int compressionBorderSizeKBytes;
  @Value("${files.maxSizeKBytes}")
  private int maxFileSizeKBytes;
  @Value("${files.compressedImageQuality}")
  private float compressedImageQuality;

  public HouseMemberDocumentSDJpaService(HouseMemberRepository houseMemberRepository,
      HouseMemberDocumentRepository houseMemberDocumentRepository) {
    this.houseMemberRepository = houseMemberRepository;
    this.houseMemberDocumentRepository = houseMemberDocumentRepository;
  }

  /**
   * Returns the HouseMemberDocument associated with the given memberId from the repository.
   * The function utilizes the Optional class to handle potential null results.
   *
   * @param memberId identifier used to locate a specific HouseMemberDocument in the repository.
   *
   * @returns an `Optional` containing a `HouseMemberDocument` object.
   */
  @Override
  public Optional<HouseMemberDocument> findHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId)
        .map(HouseMember::getHouseMemberDocument);
  }

  /**
   * Deletes a house member document by setting it to null in the database.
   *
   * @param memberId identifier of the house member document to be deleted.
   *
   * @returns a boolean indicating success or failure of the deletion operation.
   */
  @Override
  public boolean deleteHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      if (member.getHouseMemberDocument() != null) {
        member.setHouseMemberDocument(null);
        houseMemberRepository.save(member);
        return true;
      }
      return false;
    }).orElse(false);
  }

  /**
   * Updates a house member document associated with a specified member ID. It retrieves
   * the house member from the repository, creates a new document if the file is valid,
   * and adds it to the house member if present.
   *
   * @param multipartFile file being uploaded to the system.
   *
   * @param memberId identifier of the house member whose document is being updated.
   *
   * @returns an Optional containing the updated HouseMemberDocument or an empty Optional
   * if not found.
   */
  @Override
  public Optional<HouseMemberDocument> updateHouseMemberDocument(MultipartFile multipartFile,
      String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      Optional<HouseMemberDocument> houseMemberDocument = tryCreateDocument(multipartFile, member);
      houseMemberDocument.ifPresent(document -> addDocumentToHouseMember(document, member));
      return houseMemberDocument;
    }).orElse(Optional.empty());
  }

  /**
   * Creates a HouseMemberDocument based on a given MultipartFile and memberId. It
   * retrieves the HouseMember from the repository by memberId, attempts to create a
   * document, and adds it to the HouseMember if successful.
   *
   * @param multipartFile file being uploaded to be processed into a HouseMemberDocument.
   *
   * @param memberId identifier of a house member, used to retrieve the corresponding
   * entity from the repository.
   *
   * @returns an Optional containing a HouseMemberDocument if the document is created
   * successfully, or an empty Optional otherwise.
   */
  @Override
  public Optional<HouseMemberDocument> createHouseMemberDocument(MultipartFile multipartFile,
      String memberId) {
    return houseMemberRepository.findByMemberId(memberId).map(member -> {
      Optional<HouseMemberDocument> houseMemberDocument = tryCreateDocument(multipartFile, member);
      houseMemberDocument.ifPresent(document -> addDocumentToHouseMember(document, member));
      return houseMemberDocument;
    }).orElse(Optional.empty());
  }

  /**
   * Converts a multipart file to a byte stream, compresses it if necessary, and saves
   * it as a document if it meets size requirements. It returns an Optional containing
   * the saved document if successful, or an empty Optional if the operation fails or
   * the file is too large.
   *
   * @param multipartFile file uploaded by the user, which is then used to create a HouseMemberDocument.
   *
   * Decomposed, `multipartFile` is an object with the following properties:
   * - `getSize()`: Returns the size of the multipart file in bytes.
   * - `getBytes()`: Returns a byte array representation of the multipart file.
   * - `getObject()`: Returns the object associated with the multipart file.
   * - `getOriginalFilename()`: Returns the original filename of the multipart file.
   * - `getBytes()`: Returns the bytes of the multipart file.
   * - `getContentType()`: Returns the MIME type of the multipart file.
   *
   * @param member HouseMember entity whose document is being created and saved.
   *
   * Contain a `memberId` property.
   *
   * @returns an Optional containing a HouseMemberDocument object or an empty Optional.
   *
   * The returned output is an instance of `Optional<HouseMemberDocument>`.
   */
  private Optional<HouseMemberDocument> tryCreateDocument(MultipartFile multipartFile,
      HouseMember member) {

    try (ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream()) {
      BufferedImage documentImage = getImageFromMultipartFile(multipartFile);
      if (multipartFile.getSize() < DataSize.ofKilobytes(compressionBorderSizeKBytes).toBytes()) {
        writeImageToByteStream(documentImage, imageByteStream);
      } else {
        compressImageToByteStream(documentImage, imageByteStream);
      }
      if (imageByteStream.size() < DataSize.ofKilobytes(maxFileSizeKBytes).toBytes()) {
        HouseMemberDocument houseMemberDocument = saveHouseMemberDocument(imageByteStream,
            String.format("member_%s_document.jpg", member.getMemberId()));
        return Optional.of(houseMemberDocument);
      } else {
        return Optional.empty();
      }
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  /**
   * Associates a document with a HouseMember entity, saves the updated entity to the
   * database, and returns the saved entity.
   *
   * @param houseMemberDocument document associated with the HouseMember being updated.
   *
   * @param member HouseMember entity to which the given document is being associated.
   *
   * @returns the saved HouseMember entity.
   */
  private HouseMember addDocumentToHouseMember(HouseMemberDocument houseMemberDocument,
      HouseMember member) {
    member.setHouseMemberDocument(houseMemberDocument);
    return houseMemberRepository.save(member);
  }

  /**
   * Creates a new `HouseMemberDocument` instance from a byte array of an image and a
   * filename, then saves it to the repository using the `houseMemberDocumentRepository`.
   *
   * @param imageByteStream stream of bytes containing the image data to be saved in
   * the `HouseMemberDocument`.
   *
   * @param filename name of the file associated with the `HouseMemberDocument` instance
   * being saved.
   *
   * @returns a saved `HouseMemberDocument` object, which is a repository entity managed
   * by `houseMemberDocumentRepository`.
   */
  private HouseMemberDocument saveHouseMemberDocument(ByteArrayOutputStream imageByteStream,
      String filename) {
    HouseMemberDocument newDocument =
        new HouseMemberDocument(filename, imageByteStream.toByteArray());
    return houseMemberDocumentRepository.save(newDocument);
  }

  /**
   * Writes a BufferedImage to a ByteArrayOutputStream in JPEG format.
   * The image is directly written to the byte stream without intermediate steps.
   * This approach optimizes memory usage by avoiding temporary file storage.
   *
   * @param documentImage image to be written to the byte stream in the specified format.
   *
   * @param imageByteStream output stream where the image data will be written.
   */
  private void writeImageToByteStream(BufferedImage documentImage,
      ByteArrayOutputStream imageByteStream)
      throws IOException {
    ImageIO.write(documentImage, "jpg", imageByteStream);
  }

  /**
   * Compresses a BufferedImage into a byte stream in JPEG format with a specified
   * compression quality. It uses ImageIO to create an ImageOutputStream and an
   * ImageWriter, which writes the image to the stream with the specified compression
   * settings.
   *
   * @param bufferedImage image to be compressed and written to a byte stream.
   *
   * Exist a BufferedImage object with properties such as width, height, color model,
   * and graphics context.
   *
   * @param imageByteStream stream to which the compressed image is written.
   *
   * Contain a ByteArrayOutputStream, which is a byte array output stream used to store
   * the compressed image data.
   * It is used to create an ImageOutputStream for writing the image data.
   * It is used as an OutputStream for the ImageOutputStream.
   */
  private void compressImageToByteStream(BufferedImage bufferedImage,
      ByteArrayOutputStream imageByteStream) throws IOException {

    try (ImageOutputStream imageOutStream = ImageIO.createImageOutputStream(imageByteStream)) {

      ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();
      imageWriter.setOutput(imageOutStream);
      ImageWriteParam param = imageWriter.getDefaultWriteParam();

      if (param.canWriteCompressed()) {
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(compressedImageQuality);
      }
      imageWriter.write(null, new IIOImage(bufferedImage, null, null), param);
      imageWriter.dispose();
    }
  }

  /**
   * Reads an image from a multipart file and returns it as a `BufferedImage`. It uses
   * an `InputStream` to access the file's contents and `ImageIO.read` to parse the
   * image data. The function handles the `InputStream` automatically through a
   * try-with-resources statement.
   *
   * @param multipartFile uploaded file from a multipart request, providing access to
   * the file's contents.
   *
   * @returns a BufferedImage representing the image contained in the MultipartFile.
   */
  private BufferedImage getImageFromMultipartFile(MultipartFile multipartFile) throws IOException {
    try (InputStream multipartFileStream = multipartFile.getInputStream()) {
      return ImageIO.read(multipartFileStream);
    }
  }
}
