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
   * retrieves a House Member Document associated with a given member ID from the
   * repository, and maps it to an Optional<HouseMemberDocument> object.
   * 
   * @param memberId unique identifier of the member for which the corresponding house
   * member document is to be retrieved.
   * 
   * @returns an Optional object containing the HouseMemberDocument for the specified
   * member ID.
   */
  @Override
  public Optional<HouseMemberDocument> findHouseMemberDocument(String memberId) {
    return houseMemberRepository.findByMemberId(memberId)
        .map(HouseMember::getHouseMemberDocument);
  }

  /**
   * deletes a member's document from the house member repository. It first retrieves
   * the member with the specified ID, then sets the member's document to null and saves
   * the updated member object. If the document was successfully deleted, it returns
   * `true`, otherwise it returns `false`.
   * 
   * @param memberId ID of a member for which the house member document needs to be deleted.
   * 
   * @returns a boolean value indicating whether the member's document was successfully
   * deleted.
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
   * updates a house member's document by finding the member's document in the repository,
   * creating or updating it with the provided multipart file, and then adding it to
   * the member's record.
   * 
   * @param multipartFile file containing the updated House Member document to be saved.
   * 
   * @param memberId ID of the member whose House Member Document is being updated.
   * 
   * @returns an Optional<House Member Document> containing the updated document for
   * the specified member.
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
   * 1) queries the `houseMemberRepository` for a member with the given `memberId`, 2)
   * creates a new `HouseMemberDocument` using the provided `multipartFile`, and 3)
   * adds the document to the member in the repository.
   * 
   * @param multipartFile file containing the document to be generated as a HouseMemberDocument.
   * 
   * @param memberId ID of the member whose House Member Document is being created or
   * updated.
   * 
   * @returns an `Optional` object containing a `HouseMemberDocument`, created by merging
   * the provided multipart file with the member's details.
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
   * takes a multipart file and a house member as input, creates an image from the file,
   * compresses it if necessary, saves it as a document, and returns an optional document
   * object.
   * 
   * @param multipartFile MultipartFile object containing the image to be processed and
   * converted into a HouseMemberDocument.
   * 
   * @param member HouseMember for which a document is being generated.
   * 
   * @returns an optional `HouseMemberDocument` object, representing a successfully
   * created document for the given member.
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
   * updates a HouseMember object's House Member Document and persists the changes to
   * the repository, returning the updated House Member object.
   * 
   * @param houseMemberDocument House Member Document associated with the specified
   * `HouseMember` instance, and by setting it to the provided `HouseMemberDocument`,
   * the function updates the `HouseMember` instance's document association.
   * 
   * @param member HouseMember object to which the provided HouseMemberDocument will
   * be associated, by setting its `HouseMemberDocument` field to the provided document.
   * 
   * @returns a saved House Member document and member entity with the updated document
   * information.
   */
  private HouseMember addDocumentToHouseMember(HouseMemberDocument houseMemberDocument,
      HouseMember member) {
    member.setHouseMemberDocument(houseMemberDocument);
    return houseMemberRepository.save(member);
  }

  /**
   * saves a `HouseMemberDocument` object to the repository, which creates and stores
   * a new document with the given filename and image data.
   * 
   * @param imageByteStream 2D image data of the house member, which is saved to a file
   * along with the filename provided in the function `saveHouseMemberDocument`.
   * 
   * @param filename name of the file to which the `imageByteStream` contains, and is
   * used to assign it to the new `HouseMemberDocument`.
   * 
   * @returns a saved HouseMemberDocument object representing the new document with the
   * provided filename and image data.
   */
  private HouseMemberDocument saveHouseMemberDocument(ByteArrayOutputStream imageByteStream,
      String filename) {
    HouseMemberDocument newDocument =
        new HouseMemberDocument(filename, imageByteStream.toByteArray());
    return houseMemberDocumentRepository.save(newDocument);
  }

  /**
   * converts a `BufferedImage` object to a byte stream using the `ImageIO.write()`
   * method, and saves it as a JPEG image.
   * 
   * @param documentImage 2D graphics image that is to be written to a byte stream as
   * a JPEG file.
   * 
   * @param imageByteStream ByteArrayOutputStream that will store the written image data.
   */
  private void writeImageToByteStream(BufferedImage documentImage,
      ByteArrayOutputStream imageByteStream)
      throws IOException {
    ImageIO.write(documentImage, "jpg", imageByteStream);
  }

  /**
   * compresses an input `BufferedImage` using the JPEG algorithm and writes it to a `ByteArrayOutputStream`.
   * 
   * @param bufferedImage 2D image to be compressed and is used by the `ImageWriter`
   * to write the compressed image to a byte stream.
   * 
   * @param imageByteStream byte stream to which the compressed image will be written.
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
   * reads an image from an input stream provided by a `MultipartFile` object and returns
   * a `BufferedImage`.
   * 
   * @param multipartFile uploaded image file to be read and converted into an `BufferedImage`.
   * 
   * @returns a `BufferedImage`.
   */
  private BufferedImage getImageFromMultipartFile(MultipartFile multipartFile) throws IOException {
    try (InputStream multipartFileStream = multipartFile.getInputStream()) {
      return ImageIO.read(multipartFileStream);
    }
  }
}






















